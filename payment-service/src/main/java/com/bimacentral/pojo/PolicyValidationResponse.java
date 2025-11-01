package com.bimacentral.pojo;

import lombok.Data;

@Data
public class PolicyValidationResponse {
	public boolean valid;
    public String message;
    public Double dueAmount;
}
