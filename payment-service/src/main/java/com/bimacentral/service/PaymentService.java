package com.bimacentral.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bimacentral.entity.Payment;
import com.bimacentral.enums.PaymentStatus;
import com.bimacentral.pojo.PaymentEvent;
import com.bimacentral.pojo.PolicyValidationResponse;
import com.bimacentral.repository.PaymentRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final PaymentRepository repo;
	private final PaymentEventProducer producer;
	private final RestTemplate restTemplate;

	@Value("${policy.service.url}")
	private String policyServiceUrl;

	@Value("${claim.service.url}")
	private String claimServiceUrl;

	@Transactional
    public Payment processPayment(Payment payment) {
        log.info("Processing payment: {}", payment);

        // ✅ Validate policy before proceeding
        boolean valid = validatePolicyWithResilience(payment.getPolicyId());
        if (!valid) {
            payment.setStatus(PaymentStatus.FAILED);
            Payment saved = repo.save(payment);
            producer.publish(buildEvent(saved, "FAILED", "Policy not valid"));
            return saved;
        }

        // ✅ Simulate Payment Success
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setStatus(PaymentStatus.SUCCESS);
        Payment saved = repo.save(payment);

        // ✅ Publish Kafka Event
        producer.publish(buildEvent(saved, "SUCCESS", null));

        // ✅ Notify Claim Service
        if (saved.getClaimId() != null) {
            notifyClaimService(saved);
        }

        return saved;
    }

    // ----> Policy Validation (with Retry + Circuit Breaker)
    @Retry(name = "policy-validation", fallbackMethod = "policyValidationFallback")
    @CircuitBreaker(name = "policy-validation", fallbackMethod = "policyValidationFallback")
    public boolean validatePolicyWithResilience(Long policyId) {
        if (policyId == null) return true;

        String url = policyServiceUrl + "/" + policyId + "/validate";
        log.info("Calling Policy Service: {}", url);

        PolicyValidationResponse response = restTemplate.getForObject(url, PolicyValidationResponse.class);
        return response != null && response.valid;
    }

    public boolean policyValidationFallback(Long policyId, Throwable ex) {
        log.error("Fallback triggered for policy validation (policyId={}): {}", policyId, ex.getMessage());
        return false;
    }

    // ----> Claim Settlement (with Retry + Circuit Breaker)
    @Retry(name = "claim-settlement", fallbackMethod = "claimSettlementFallback")
    @CircuitBreaker(name = "claim-settlement", fallbackMethod = "claimSettlementFallback")
    public void notifyClaimService(Payment payment) {
        String url = claimServiceUrl + "/" + payment.getClaimId() + "/settle";
        log.info("Notifying Claim Service: {}", url);

        Map<String, Object> body = new HashMap<>();
        body.put("transactionId", payment.getTransactionId());
        body.put("amount", payment.getAmount());
        body.put("status", "SETTLED");

        restTemplate.postForEntity(url, body, Void.class);
    }

    public void claimSettlementFallback(Payment payment, Throwable ex) {
        log.error("Fallback triggered for claim settlement (claimId={}): {}", payment.getClaimId(), ex.getMessage());
        // You could add retry queue or save to DB for later reprocessing
    }

    private PaymentEvent buildEvent(Payment p, String status, String reason) {
        return PaymentEvent.builder()
                .transactionId(p.getTransactionId())
                .paymentId(p.getId())
                .policyId(p.getPolicyId())
                .claimId(p.getClaimId())
                .amount(p.getAmount())
                .status(status)
                .reason(reason)
                .build();
    }

	public Payment initiatePayment(Payment payment) {
		payment.setTransactionId(UUID.randomUUID().toString());
		payment.setStatus(PaymentStatus.SUCCESS);
		return repo.save(payment);
	}

	public Payment getByTransactionId(String transactionId) {
		return repo.findByTransactionId(transactionId);
	}

	public List<Payment> getAllPayments() {
		return repo.findAll();
	}

	public void deletePayment(Long id) {
		repo.deleteById(id);
	}
}
