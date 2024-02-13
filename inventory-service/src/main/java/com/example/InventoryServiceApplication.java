package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

class InventoryCheckResponse {
	private boolean available;

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
}

@RestController
class InventoryController {

	@GetMapping("/inventory/{item}/check")
	public InventoryCheckResponse checkInventory(@PathVariable String item) {
		// ... check inventory
		InventoryCheckResponse response = new InventoryCheckResponse();
		if (item.equals("item-1")) {
			response.setAvailable(true);
		}
		return response;

	}
}

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
