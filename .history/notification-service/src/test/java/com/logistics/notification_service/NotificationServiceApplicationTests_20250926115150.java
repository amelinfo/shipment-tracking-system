package com.logistics.notification_service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
		// Simple test that doesn't require full Spring context
		// In a real scenario, this would test application startup
		assertDoesNotThrow(() -> {
			NotificationServiceApplication.main(new String[]{});
		});
	}

}
