package com.bimacentral.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bimacentral.entity.Payment;
import com.bimacentral.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	public ResponseEntity<Payment> makePayment(@RequestBody Payment payment) {
		Payment saved = paymentService.processPayment(payment);
		return ResponseEntity.ok(saved);
	}

	@GetMapping
	public List<Payment> getAllPayments() {
		return paymentService.getAllPayments();
	}

	@GetMapping("/{txnId}")
	public Payment getByTransactionId(@PathVariable String txnId) {
		return paymentService.getByTransactionId(txnId);
	}

	@DeleteMapping("/{id}")
	public void deletePayment(@PathVariable Long id) {
		paymentService.deletePayment(id);
	}

}
