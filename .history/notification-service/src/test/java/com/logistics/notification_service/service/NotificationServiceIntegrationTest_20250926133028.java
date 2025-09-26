package com.logistics.notification_service.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistics.common.ShipmentEvent;
import com.logistics.notification_service.config.NotificationMetrics;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceIntegrationTest {

    @Mock
    private NotificationMetrics notificationMetrics;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void processShipmentEvent_ValidEvent_ProcessesSuccessfully() {
        // Given
        ShipmentEvent event = new ShipmentEvent("TEST123", "CREATED", "IN_TRANSIT");

        // When
        notificationService.processShipmentEvent(event);

        // Then
        verify(notificationMetrics).incrementProcessedCounter();
        // Should not throw exception
    }

    @Test
    void processShipmentEvent_FirstStatusEvent_ProcessesSuccessfully() {
        // Given
        ShipmentEvent event = new ShipmentEvent("TEST123", null, "CREATED");

        // When
        notificationService.processShipmentEvent(event);

        // Then
        verify(notificationMetrics).incrementProcessedCounter();
    }

    @Test
    void buildNotificationMessage_StatusChange_ReturnsCorrectMessage() {
        // Given
        ShipmentEvent event = new ShipmentEvent("TEST123", "IN_TRANSIT", "DELIVERED");

        // When
        String message = notificationService.buildNotificationMessage(event);

        // Then
        assertNotNull(message);
        assertTrue(message.contains("TEST123"));
        assertTrue(message.contains("IN_TRANSIT"));
        assertTrue(message.contains("DELIVERED"));
        assertTrue(message.contains("Status changed"));
    }

    @Test
    void buildNotificationMessage_FirstStatus_ReturnsCorrectMessage() {
        // Given
        ShipmentEvent event = new ShipmentEvent("TEST123", null, "CREATED");

        // When
        String message = notificationService.buildNotificationMessage(event);

        // Then
        assertNotNull(message);
        assertTrue(message.contains("TEST123"));
        assertTrue(message.contains("CREATED"));
        assertTrue(message.contains("initialized"));
    }
}