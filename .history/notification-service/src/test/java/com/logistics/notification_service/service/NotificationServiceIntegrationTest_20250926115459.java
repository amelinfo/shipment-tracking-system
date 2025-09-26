package com.logistics.notification_service.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistics.common.ShipmentEvent;
import com.logistics.notification_service.config.NotificationMetrics;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceIntegrationTest {

    @Mock
    private NotificationMetrics notificationMetrics;
    
    private NotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationMetrics);
    }

    @Test
    void testProcessShipmentEvent_FirstStatus() {
        // Given
        ShipmentEvent event = new ShipmentEvent("TEST123", null, "CREATED");
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> notificationService.processShipmentEvent(event));
        
        // Verify metrics were called
        verify(notificationMetrics, times(1)).incrementProcessedCounter();
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
