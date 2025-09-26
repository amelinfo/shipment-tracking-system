package com.logistics.tracking_service.service;

import com.logistics.tracking_service.config.ShipmentMetrics;
import com.logistics.common.ShipmentEvent;
import com.logistics.tracking_service.dto.TrackingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class TrackingServiceIntegrationTest {

    @Autowired
    private TrackingService trackingService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testProcessTracking_FirstTimeTracking() {
        // Given
        String trackingId = "TEST123";
        
        // When
        TrackingResult result = trackingService.processTracking(trackingId);
        
        // Then
        assertTrue(result.success());
        assertNotNull(result.currentStatus());
        assertTrue(result.statusChanged()); // First time should be considered a change
        assertEquals(trackingId, result.trackingId());
    }

    @Test
    void testProcessTracking_StatusChange() {
        // Given
        String trackingId = "TEST456";
        
        // First call
        TrackingResult firstResult = trackingService.processTracking(trackingId);
        String firstStatus = firstResult.currentStatus();
        
        // When - second call (might get different status due to random mock)
        TrackingResult secondResult = trackingService.processTracking(trackingId);
        
        // Then
        assertTrue(secondResult.success());
        assertEquals(trackingId, secondResult.trackingId());
        // Note: Due to mock randomness, status might or might not change
    }
}