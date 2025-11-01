package com.bimacentral.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
	private String transactionId;
	private Long paymentId;
	private Long policyId;
	private Long claimId; // nullable
	private Double amount;
	private String status; // PENDING / SUCCESS / FAILED
	private String reason; // optional failure reason
	private String email; // optional customer email

}
