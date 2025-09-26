package com.logistics.notification_service.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.logistics.common.ShipmentEvent;
import com.logistics.tracking_service.dto.TrackingResult;C:\Users\taleb\shipment-tracking-system\tracking-service\src\main\java\com\logistics\tracking_service\dto\TrackingResult.java

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092"
})
public class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    void testProcessShipmentEvent_FirstStatus() {
        // Given
        ShipmentEvent event = new ShipmentEvent("TEST123", null, "CREATED");
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> notificationService.processShipmentEvent(event));
    }

    @Test
    void testProcessShipmentEvent_StatusChange() {
         // Given
    String trackingId = "TEST456";
    
    // First call
    TrackingResult firstResult = trackingService.processTracking(trackingId);
    // Variable removed - no longer unused
    
    // When - second call
    TrackingResult secondResult = trackingService.processTracking(trackingId);
    
    // Then
    assertTrue(secondResult.success());
    assertEquals(trackingId, secondResult.trackingId());
    assertNotNull(secondResult.currentStatus());
    }

    @Test
    void testBuildNotificationMessage() {
        // Given
        ShipmentEvent event = new ShipmentEvent("TEST789", "IN_TRANSIT", "DELIVERED");
        
        // When
        String message = notificationService.buildNotificationMessage(event);
        
        // Then
        assertTrue(message.contains("TEST789"));
        assertTrue(message.contains("IN_TRANSIT"));
        assertTrue(message.contains("DELIVERED"));
        assertTrue(message.contains("Status changed"));
    }
}
