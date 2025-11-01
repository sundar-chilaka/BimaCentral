package com.bimacentral.entity;

import java.time.LocalDateTime;

import com.bimacentral.enums.PaymentStatus;
import com.bimacentral.enums.PaymentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String transactionId;
	private Long policyId;
	private Long claimId;
	private Double amount;

	@Enumerated(EnumType.STRING)
	private PaymentType type;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	private String paymentMethod;

	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		status = PaymentStatus.PENDING;
	}
}
