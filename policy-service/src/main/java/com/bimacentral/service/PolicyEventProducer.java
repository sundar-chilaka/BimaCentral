package com.bimacentral.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PolicyEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public PolicyEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendPolicyEvent(Object event) {
		kafkaTemplate.send("policy-events", event);
	}
}
