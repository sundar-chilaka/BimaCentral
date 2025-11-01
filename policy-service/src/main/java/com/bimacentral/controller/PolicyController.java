package com.bimacentral.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bimacentral.entity.Policy;
import com.bimacentral.repository.PolicyRepository;
import com.bimacentral.service.PolicyEventProducer;

@RestController
@RequestMapping("/policies")
public class PolicyController {

	private final PolicyRepository policyRepository;
	private final PolicyEventProducer producer;

	public PolicyController(PolicyRepository policyRepository, PolicyEventProducer producer) {
		this.policyRepository = policyRepository;
		this.producer = producer;
	}

	@PostMapping
	public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
		Policy saved = policyRepository.save(policy);
		producer.sendPolicyEvent(saved);
		return ResponseEntity.ok(saved);
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<Optional<Policy>> getByCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(policyRepository.findByCustomerId(customerId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Policy> getById(@PathVariable Long id) {
		return policyRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}
}