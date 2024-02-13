package com.example;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class PaymentServiceMessageHandler {

	private final KafkaTemplate<String, String> kafkaTemplate;

	private static Map<String, Double> customerBalances = new HashMap<>();
	static {
		customerBalances.put("customer1", 1000.0);
		customerBalances.put("customer2", 1000.0);
		customerBalances.put("customer3", 1000.0);
		customerBalances.put("customer4", 1000.0);
		customerBalances.put("customer5", 1000.0);
	}

	@SuppressWarnings("null")
	@KafkaListener(topics = "payment_requests", groupId = "payment-service")
	public void handleMessage(ConsumerRecord<String, String> record) {

		String orderId = record.key() != null ? record.key() : "unknown";
		String payload = record.value();
		String[] items = payload.split(",");
		String customerId = items[0];
		double amount = Integer.parseInt(items[1]);

		if (customerBalances.get(customerId) >= amount) {
			customerBalances.put(customerId, customerBalances.get(customerId) - amount);
			kafkaTemplate.send("payment_responses", orderId, "APPROVED");
		} else {
			kafkaTemplate.send("payment_responses", orderId, "DECLINED");
		}

	}
}

@SpringBootApplication
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
