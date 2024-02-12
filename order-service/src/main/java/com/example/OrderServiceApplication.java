package com.example;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

enum OrderStatus {
	PLACED, APPROVED, CANCELLED
}

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "orders")
class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String[] items;
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
}

@Data
class NewOrderRequest {
	private String[] items;
}

@NoArgsConstructor
@AllArgsConstructor
@Data
class NewOrderResponse {
	private int id;
	private OrderStatus status;
}

class InventoryCheckResponse {
	private boolean available;

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
}

interface OrdersRepository extends JpaRepository<Order, Integer> {
	// ... save order
}

@RestController
@AllArgsConstructor
class OrdersController {

	private final OrdersRepository ordersRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	@PostMapping("/orders")
	public ResponseEntity<?> createOrder(@RequestBody NewOrderRequest newOrder) {
		// ... create order

		// check order items exist in inventory
		String item = newOrder.getItems()[0];
		String inventoryApi = "http://localhost:7002/inventory/" + item + "/check";
		ResponseEntity<InventoryCheckResponse> inventoryCheckResponseEntity = restTemplate.getForEntity(inventoryApi,
				InventoryCheckResponse.class);
		InventoryCheckResponse inventoryCheckResponse = inventoryCheckResponseEntity.getBody();
		if (inventoryCheckResponse.isAvailable()) {
			Order order = new Order(0, newOrder.getItems(), OrderStatus.PLACED);
			// ... save order
			ordersRepository.save(order);
			// ... return response
			return ResponseEntity.ok(new NewOrderResponse(order.getId(), order.getStatus()));
		} else {
			return ResponseEntity.status(400).build();
		}
	}

	@GetMapping("/orders/{id}")
	public Order getOrder(@PathVariable int id) {
		// ... get order
		Order order = ordersRepository.findById(id).orElseThrow();
		// ... return response
		return order;
	}

	@GetMapping("/orders")
	public java.util.List<Order> getOrders() {
		// ... get orders
		List<Order> orders = ordersRepository.findAll();
		// ... return response
		return orders;
	}
	// ... other methods
}

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
