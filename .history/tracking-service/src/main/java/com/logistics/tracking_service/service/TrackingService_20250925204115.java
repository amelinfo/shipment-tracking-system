package com.logistics.tracking_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.logistics.tracking_service.dto.ShipmentEvent;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TrackingService {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackingService.class);
    
    private final ExternalShipmentService externalShipmentService;
    private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
    private final ShipmentMetrics shipmentMetrics;
    
    // In-memory storage for previous status (for demo purposes)
    // In production, use a database
    private final Map<String, String> shipmentStatusCache = new ConcurrentHashMap<>();
    
    public TrackingService(ExternalShipmentService externalShipmentService,
                          KafkaTemplate<String, ShipmentEvent> kafkaTemplate,
                          ShipmentMetrics shipmentMetrics) {
        this.externalShipmentService = externalShipmentService;
        this.kafkaTemplate = kafkaTemplate;
        this.shipmentMetrics = shipmentMetrics;
    }
    
    /**
     * Process tracking request: check status, detect changes, publish events
     */
    public TrackingResult processTracking(String trackingId) {
        logger.info("Processing tracking request for: {}", trackingId);
        
        // Start timer for external API call
        var timerSample = shipmentMetrics.startApiTimer();
        
        try {
            // Get current status from external API
            String currentStatus = externalShipmentService.getShipmentStatus(trackingId);
            logger.debug("Current status for {}: {}", trackingId, currentStatus);
            
            // Get previous status from cache
            String previousStatus = shipmentStatusCache.get(trackingId);
            
            // Check if status has changed
            boolean statusChanged = hasStatusChanged(previousStatus, currentStatus);
            
            if (statusChanged) {
                // Publish event to Kafka
                publishStatusChangeEvent(trackingId, previousStatus, currentStatus);
                
                // Update cache with new status
                shipmentStatusCache.put(trackingId, currentStatus);
                
                logger.info("Status change detected for {}: {} -> {}", 
                    trackingId, previousStatus, currentStatus);
                
                return TrackingResult.success(trackingId, currentStatus, true, 
                    "Status changed from " + previousStatus + " to " + currentStatus);
            } else {
                logger.debug("No status change for {}: {}", trackingId, currentStatus);
                return TrackingResult.success(trackingId, currentStatus, false, 
                    "Status unchanged: " + currentStatus);
            }
            
        } catch (Exception e) {
            logger.error("Error processing tracking request for {}: {}", trackingId, e.getMessage());
            shipmentMetrics.recordApiLatency(0, TimeUnit.MILLISECONDS); // Record failure
            return TrackingResult.error(trackingId, "Failed to process tracking request: " + e.getMessage());
        } finally {
            // Stop timer and record latency
            shipmentMetrics.stopApiTimer(timerSample);
        }
    }
    
    private boolean hasStatusChanged(String previousStatus, String currentStatus) {
        // Handle initial status (no previous status)
        if (previousStatus == null) {
            return true; // First time tracking, consider it a "change"
        }
        
        return !previousStatus.equals(currentStatus);
    }
    
    private void publishStatusChangeEvent(String trackingId, String oldStatus, String newStatus) {
        try {
            ShipmentEvent event = ShipmentEvent.of(trackingId, oldStatus, newStatus);
            
            // Send to Kafka topic
            kafkaTemplate.send("shipment-status-changes", trackingId, event)
                .addCallback(
                    result -> {
                        logger.debug("Successfully published event for {}: {}", trackingId, event);
                        shipmentMetrics.incrementStatusChangeCounter();
                    },
                    failure -> logger.error("Failed to publish event for {}: {}", trackingId, failure.getMessage())
                );
            
        } catch (Exception e) {
            logger.error("Error publishing Kafka event for {}: {}", trackingId, e.getMessage());
        }
    }
    
    /**
     * Get current status for a tracking ID (without triggering checks)
     */
    public String getCurrentStatus(String trackingId) {
        return shipmentStatusCache.get(trackingId);
    }
    
    /**
     * Get all tracked shipments (for debugging/monitoring)
     */
    public Map<String, String> getTrackedShipments() {
        return Map.copyOf(shipmentStatusCache);
    }
}