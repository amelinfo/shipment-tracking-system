package com.logistics.tracking_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.logistics.common.ShipmentEvent;
import com.logistics.tracking_service.config.ShipmentMetrics;
import com.logistics.tracking_service.dto.TrackingResult;
import com.logistics.tracking_service.entity.Shipment;
import com.logistics.tracking_service.repository.ShipmentRepository;
import com.logistics.tracking_service.service.TrackingService.ShipmentHistory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TrackingService {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackingService.class);
    
    private final ExternalShipmentService externalShipmentService;

    private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;

    private final ShipmentMetrics shipmentMetrics;
    private final ShipmentRepository shipmentRepository;
    
    public TrackingService(ExternalShipmentService externalShipmentService,
                          KafkaTemplate<String, ShipmentEvent> kafkaTemplate,
                          ShipmentMetrics shipmentMetrics,
                          ShipmentRepository shipmentRepository) {
        this.externalShipmentService = externalShipmentService;
        this.kafkaTemplate = kafkaTemplate;
        this.shipmentMetrics = shipmentMetrics;
        this.shipmentRepository = shipmentRepository;
    }
    
    public TrackingResult processTracking(String trackingId) {
        logger.info("Processing tracking request for: {}", trackingId);
        
        var timerSample = shipmentMetrics.startApiTimer();
        
        try {
            String currentStatus = externalShipmentService.getShipmentStatus(trackingId);
            logger.debug("Current status for {}: {}", trackingId, currentStatus);
            
            var result = shipmentRepository.findByTrackingId(trackingId)
                .map(existing -> updateExistingShipment(existing, currentStatus))
                .orElseGet(() -> createNewShipment(trackingId, currentStatus));
            
            logger.info("Tracking processed for {}: {}", trackingId, result.message());
            return result;
            
        } catch (Exception e) {
            logger.error("Error processing tracking request for {}: {}", trackingId, e.getMessage());
            shipmentMetrics.recordApiLatency(0, java.util.concurrent.TimeUnit.MILLISECONDS);
            return TrackingResult.error(trackingId, "Failed to process tracking request: " + e.getMessage());
        } finally {
            shipmentMetrics.stopApiTimer(timerSample);
        }
    }
    
    private TrackingResult updateExistingShipment(Shipment existing, String newStatus) {
        boolean statusChanged = !existing.currentStatus().equals(newStatus);
        
        if (statusChanged) {
            var updatedShipment = existing.withStatusUpdate(newStatus);
            shipmentRepository.save(updatedShipment);
            publishStatusChangeEvent(updatedShipment);
            
            return TrackingResult.statusChanged(
                updatedShipment.trackingId(), 
                existing.currentStatus(), 
                newStatus
            );
        } else {
            var updatedShipment = new Shipment(
                existing.trackingId(),
                existing.currentStatus(),
                existing.previousStatus(),
                existing.createdAt(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                existing.checkCount() + 1,
                existing.metadata()
            );
            shipmentRepository.save(updatedShipment);
            
            return TrackingResult.statusUnchanged(updatedShipment.trackingId(), newStatus);
        }
    }
    
    private TrackingResult createNewShipment(String trackingId, String status) {
        var newShipment = Shipment.of(trackingId, status);
        shipmentRepository.save(newShipment);
        publishStatusChangeEvent(newShipment);
        
        return TrackingResult.initialStatus(trackingId, status);
    }
    
    private void publishStatusChangeEvent(Shipment shipment) {
        logger.info("""
            Publishing status change event:
            Tracking ID: {}
            Previous Status: {}
            New Status: {}
            Timestamp: {}
            """, shipment.trackingId(), shipment.previousStatus(), shipment.currentStatus(), shipment.updatedAt());
        
        var event = new ShipmentEvent(
            shipment.trackingId(),
            shipment.previousStatus(),
            shipment.currentStatus()
        );
        
        kafkaTemplate.send("shipment-status-changes", shipment.trackingId(), event);
        shipmentMetrics.incrementStatusChangeCounter();
    }
    
    public Optional<String> getCurrentStatus(String trackingId) {
        return shipmentRepository.findByTrackingId(trackingId)
            .map(Shipment::currentStatus);
    }
    
    public java.util.List<ShipmentHistory> getShipmentHistory(String trackingId) {
        return shipmentRepository.findByTrackingId(trackingId)
            .stream()
            .<ShipmentHistory>mapMulti((shipment, consumer) -> {
                shipment.getPreviousStatus().ifPresent(prevStatus -> 
                    consumer.accept(new ShipmentHistory(prevStatus, shipment.updatedAt()))
                );
                consumer.accept(new ShipmentHistory(shipment.currentStatus(), shipment.updatedAt()));
            })
            .toList();
    }
    
    public Map<String, String> getTrackedShipments() {
    return shipmentRepository.findAll().stream()
        .collect(java.util.stream.Collectors.toMap(
            Shipment::trackingId,
            Shipment::currentStatus
        ));
}
    public record ShipmentHistory(String status, LocalDateTime timestamp) {}
}