package com.logistics.tracking_service.service;

import com.logistics.tracking_service.config.ShipmentMetrics;
import com.logistics.common.ShipmentEvent;
import com.logistics.tracking_service.dto.TrackingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TrackingServiceTest {

    private ExternalShipmentService externalShipmentService;
    private KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
    private ShipmentMetrics shipmentMetrics;
    private TrackingService trackingService;

    @BeforeEach
    void setUp() {
        externalShipmentService = mock(ExternalShipmentService.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        shipmentMetrics = mock(ShipmentMetrics.class);

        when(shipmentMetrics.startApiTimer()).thenReturn(new Object());
        doNothing().when(shipmentMetrics).stopApiTimer(any());
        doNothing().when(shipmentMetrics).recordApiLatency(anyLong(), any(TimeUnit.class));
        doNothing().when(shipmentMetrics).incrementStatusChangeCounter();

        trackingService = new TrackingService(externalShipmentService, kafkaTemplate, shipmentMetrics);
    }

    @Test
    void testProcessTracking_FirstTime_StatusChange() {
        String trackingId = "ABC123";
        String currentStatus = "IN_TRANSIT";

        when(externalShipmentService.getShipmentStatus(trackingId)).thenReturn(currentStatus);
        when(kafkaTemplate.send(anyString(), anyString(), any(ShipmentEvent.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        TrackingResult result = trackingService.processTracking(trackingId);

        assertTrue(result.isStatusChanged());
        assertEquals(currentStatus, result.getCurrentStatus());
        assertEquals(trackingId, result.getTrackingId());
        assertEquals("Status changed from null to IN_TRANSIT", result.getMessage());
        assertEquals(currentStatus, trackingService.getCurrentStatus(trackingId));
    }

    @Test
    void testProcessTracking_NoStatusChange() {
        String trackingId = "DEF456";
        String status = "DELIVERED";

        // Simulate previous status in cache
        trackingService.getTrackedShipments().put(trackingId, status);

        when(externalShipmentService.getShipmentStatus(trackingId)).thenReturn(status);

        TrackingResult result = trackingService.processTracking(trackingId);

        assertFalse(result.isStatusChanged());
        assertEquals(status, result.getCurrentStatus());
        assertEquals("Status unchanged: DELIVERED", result.getMessage());
    }

    @Test
    void testProcessTracking_StatusChangeDetected() {
        String trackingId = "XYZ789";
        String oldStatus = "IN_TRANSIT";
        String newStatus = "DELIVERED";

        // Simulate previous status in cache
        trackingService.getTrackedShipments().put(trackingId, oldStatus);

        when(externalShipmentService.getShipmentStatus(trackingId)).thenReturn(newStatus);
        when(kafkaTemplate.send(anyString(), anyString(), any(ShipmentEvent.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        TrackingResult result = trackingService.processTracking(trackingId);

        assertTrue(result.isStatusChanged());
        assertEquals(newStatus, result.getCurrentStatus());
        assertEquals("Status changed from IN_TRANSIT to DELIVERED", result.getMessage());
        assertEquals(newStatus, trackingService.getCurrentStatus(trackingId));
    }

    @Test
    void testProcessTracking_ExternalServiceThrowsException() {
        String trackingId = "ERR001";
        when(externalShipmentService.getShipmentStatus(trackingId)).thenThrow(new RuntimeException("API error"));

        TrackingResult result = trackingService.processTracking(trackingId);

        assertFalse(result.isStatusChanged());
        assertEquals(trackingId, result.getTrackingId());
        assertTrue(result.getMessage().contains("Failed to process tracking request"));
        assertNull(trackingService.getCurrentStatus(trackingId));
    }

    @Test
    void testGetTrackedShipments_ReturnsCopy() {
        String trackingId = "SHIP123";
        String status = "IN_TRANSIT";
        trackingService.getTrackedShipments().put(trackingId, status);

        Map<String, String> tracked = trackingService.getTrackedShipments();
        assertTrue(tracked.containsKey(trackingId));
        assertEquals(status, tracked.get(trackingId));
    }
}