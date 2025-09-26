package com.logistics.tracking_service.controller;

import com.logistics.tracking_service.dto.TrackingRequest;
import com.logistics.tracking_service.dto.TrackingResult;
import com.logistics.tracking_service.service.TrackingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TrackingController {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackingController.class);
    
    private final TrackingService trackingService;
    
    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }
    
    @PostMapping("/trackings")
    public ResponseEntity<TrackingResult> trackShipment(
            @Valid @RequestBody TrackingRequest request) {
        
        logger.info("Received tracking request for ID: {}", request.trackingId());
        
        try {
            TrackingResult result = trackingService.processTracking(request.trackingId());
            
            if (result.success()) {
                logger.info("Tracking processed successfully for {}: {}", 
                    request.trackingId(), result.message());
                return ResponseEntity.ok(result);
            } else {
                logger.warn("Tracking processing failed for {}: {}", 
                    request.trackingId(), result.message());
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error processing tracking request for {}: {}", 
                request.trackingId(), e.getMessage());
            
            TrackingResult errorResult = TrackingResult.error(
                request.trackingId(), 
                "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
    
    @GetMapping("/trackings/{trackingId}")
    public ResponseEntity<Map<String, String>> getShipmentStatus(
            @PathVariable String trackingId) {
        
        logger.debug("Fetching current status for: {}", trackingId);
        
        String currentStatus = trackingService.getCurrentStatus(trackingId);
        
        if (currentStatus != null) {
            return ResponseEntity.ok(Map.of(
                "trackingId", trackingId,
                "currentStatus", currentStatus
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/admin/trackings")
    public ResponseEntity<Map<String, String>> getAllTrackedShipments() {
        // For monitoring/debugging purposes
        Map<String, String> shipments = trackingService.getTrackedShipments();
        logger.debug("Returning {} tracked shipments", shipments.size());
        return ResponseEntity.ok(shipments);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "tracking-service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}