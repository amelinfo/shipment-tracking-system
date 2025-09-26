package com.logistics.tracking_service.service;

import com.logistics.common.ShipmentEvent;
import com.logistics.tracking_service.config.ShipmentMetrics;
import com.logistics.tracking_service.dto.TrackingResult;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceIntegrationTest {

    @Mock
    private ExternalShipmentService externalShipmentService;
    
    @Mock
    private KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
    
    @Mock
    private ShipmentMetrics shipmentMetrics;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private Timer.Sample timerSample;

    private TrackingService trackingService;

    @BeforeEach
    void setUp() {
        trackingService = new TrackingService(externalShipmentService, kafkaTemplate, shipmentMetrics);
        
        // Setup default mock behavior
        when(shipmentMetrics.startApiTimer()).thenReturn(timerSample);
        doNothing().when(shipmentMetrics).stopApiTimer(any());
    }

    @Test
    void testProcessTracking_FirstTimeTracking() {
        // Given
        String trackingId = "TEST123";
        String expectedStatus = "IN_TRANSIT";
        when(externalShipmentService.getShipmentStatus(trackingId)).thenReturn(expectedStatus);
        
        // When
        TrackingResult result = trackingService.processTracking(trackingId);
        
        // Then
        assertTrue(result.success());
        assertEquals(expectedStatus, result.currentStatus());
        assertTrue(result.statusChanged()); // First time should be considered a change
        assertEquals(trackingId, result.trackingId());
        
        // Verify interactions
        verify(externalShipmentService).getShipmentStatus(trackingId);
        verify(shipmentMetrics).startApiTimer();
        verify(shipmentMetrics).stopApiTimer(any());
        verify(kafkaTemplate).send(eq("shipment-status-changes"), eq(trackingId), any(ShipmentEvent.class));
    }

    @Test
    void testProcessTracking_StatusChange() {
        // Given
        String trackingId = "TEST456";
        String firstStatus = "CREATED";
        String secondStatus = "IN_TRANSIT";
        
        when(externalShipmentService.getShipmentStatus(trackingId))
            .thenReturn(firstStatus)
            .thenReturn(secondStatus);
        
        // First call
        TrackingResult firstResult = trackingService.processTracking(trackingId);
        assertTrue(firstResult.success());
        assertEquals(firstStatus, firstResult.currentStatus());
        assertTrue(firstResult.statusChanged());
        
        // When - Second call
        TrackingResult secondResult = trackingService.processTracking(trackingId);
        
        // Then
        assertTrue(secondResult.success());
        assertEquals(secondStatus, secondResult.currentStatus());
        assertTrue(secondResult.statusChanged()); // Status should have changed from CREATED to IN_TRANSIT
        assertEquals(trackingId, secondResult.trackingId());
        
        // Verify interactions
        verify(externalShipmentService, times(2)).getShipmentStatus(trackingId);
        verify(kafkaTemplate, times(2)).send(eq("shipment-status-changes"), eq(trackingId), any(ShipmentEvent.class));
    }

    @Test
    void testProcessTracking_NoStatusChange() {
        // Given
        String trackingId = "TEST789";
        String status = "DELIVERED";
        
        when(externalShipmentService.getShipmentStatus(trackingId)).thenReturn(status);
        
        // First call
        TrackingResult firstResult = trackingService.processTracking(trackingId);
        assertTrue(firstResult.success());
        
        // When - Second call with same status
        TrackingResult secondResult = trackingService.processTracking(trackingId);
        
        // Then
        assertTrue(secondResult.success());
        assertEquals(status, secondResult.currentStatus());
        assertFalse(secondResult.statusChanged()); // No status change expected
        assertEquals(trackingId, secondResult.trackingId());
        
        // Verify only one Kafka message sent (first time only)
        verify(kafkaTemplate, times(1)).send(eq("shipment-status-changes"), eq(trackingId), any(ShipmentEvent.class));
    }
}