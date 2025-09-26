package com.logistics.notification_service.service;

import com.logistics.common.ShipmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ShipmentEventConsumer.class);
    
    private final NotificationService notificationService;
    
    public ShipmentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @KafkaListener(
        topics = "shipment-status-changes",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleShipmentStatusChange(
            @Payload ShipmentEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Received shipment event [key: {}, partition: {}, offset: {}]: {}",
            key, partition, offset, event);
        
        try {
            // Process the notification
            notificationService.processShipmentEvent(event);
            
            // Simulate sending to different channels
            notificationService.sendToDifferentChannels(event);
            
            logger.debug("Successfully consumed event for shipment {}", event.trackingId());
            
        } catch (Exception e) {
            logger.error("Failed to process shipment event for {}: {}", 
                event.trackingId(), e.getMessage());
            // The exception will be handled by Kafka error handling mechanism
            throw new RuntimeException("Notification processing failed", e);
        }
    }
    
    /**
     * Additional listener for dead letter topic (error handling)
     */
    @KafkaListener(
        topics = "shipment-status-changes.DLT", // Dead Letter Topic
        groupId = "notification-service-dlt-group"
    )
    public void handleDeadLetterEvent(
            @Payload ShipmentEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        
        logger.error("Processing dead letter event for shipment {}: {}", 
            key, event);
        
        // In production, you might want to:
        // - Send alert to operations team
        // - Store in database for manual processing
        // - Retry with exponential backoff
        
        logger.warn("Manual intervention required for failed shipment notification: {}", event.trackingId());
    }
}