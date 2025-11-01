package com.bimacentral.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bimacentral.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Payment findByTransactionId(String transactionId);

}
