package com.bimacentral.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bimacentral.pojo.PaymentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
	private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
	private final String topic = "payment-events";

	public void publish(PaymentEvent event) {
		log.info("Publishing payment event: {}", event);
		kafkaTemplate.send(topic, event.getTransactionId(), event).whenComplete((result, ex) -> {
			if (ex != null)
				log.error("Failed to send payment event", ex);
			else
				log.info("Payment event sent to partition {}, offset {}", result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
		});
	}

}
