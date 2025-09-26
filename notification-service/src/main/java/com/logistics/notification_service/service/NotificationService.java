package com.logistics.notification_service.service;

import com.logistics.common.ShipmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.logistics.notification_service.config.NotificationMetrics;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationMetrics notificationMetrics;
    
    public NotificationService(NotificationMetrics notificationMetrics) {
        this.notificationMetrics = notificationMetrics;
    }
    
    /**
     * Process shipment status change event and send notification
     */
    public void processShipmentEvent(ShipmentEvent event) {
        logger.info("Processing shipment event: {}", event);
        
        try {
            // Log the notification (in real system, this would send email/SMS/webhook)
            logNotification(event);
            
            // Increment success metric
            notificationMetrics.incrementProcessedCounter();
            
            logger.info("Successfully processed notification for shipment [{}]: Status changed from '{}' to '{}'",
                event.trackingId(), 
                event.oldStatus() != null ? event.oldStatus() : "N/A", 
                event.newStatus());
                
        } catch (Exception e) {
            logger.error("Error processing notification for shipment [{}]: {}", 
                event.trackingId(), e.getMessage());
            notificationMetrics.incrementErrorCounter();
            throw e; // Re-throw to enable Kafka retry mechanism
        }
    }
    
    private void logNotification(ShipmentEvent event) {
        String notificationMessage = buildNotificationMessage(event);
        
        // In a real system, this would send to various channels:
        // - Email notification
        // - SMS alert  
        // - Webhook to external systems
        // - Mobile push notification
        // - Internal dashboard update
        
        logger.info("=== SHIPMENT NOTIFICATION ===");
        logger.info("Recipient: Customer/Internal System");
        logger.info("Subject: Shipment Status Update");
        logger.info("Message: {}", notificationMessage);
        logger.info("=== END NOTIFICATION ===");
    }
    
    private String buildNotificationMessage(ShipmentEvent event) {
        StringBuilder message = new StringBuilder();
        
        message.append("Shipment tracking number: ").append(event.trackingId()).append("\n");
        
        if (event.oldStatus() == null) {
            message.append("Status: ").append(event.newStatus()).append("\n");
            message.append("Your shipment has been initialized and is now ").append(event.newStatus().toLowerCase());
        } else {
            message.append("Status changed from '").append(event.oldStatus())
                   .append("' to '").append(event.newStatus()).append("'\n");
            message.append("Your shipment status has been updated.");
        }
        
        message.append("\nTimestamp: ").append(event.timestamp());
        message.append("\n\nThank you for using our logistics service!");
        
        return message.toString();
    }
    
    /**
     * Simulate different notification channels (for demonstration)
     */
    public void sendToDifferentChannels(ShipmentEvent event) {
        // Simulate email notification
        simulateEmailNotification(event);
        
        // Simulate SMS notification
        simulateSmsNotification(event);
        
        // Simulate webhook to external systems
        simulateWebhookNotification(event);
    }
    
    private void simulateEmailNotification(ShipmentEvent event) {
        logger.debug("[EMAIL] Sending notification for shipment {} to customer", event.trackingId());
        // Simulate email sending delay
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulateSmsNotification(ShipmentEvent event) {
        logger.debug("[SMS] Sending SMS alert for shipment {}", event.trackingId());
        // Simulate SMS sending delay
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulateWebhookNotification(ShipmentEvent event) {
        logger.debug("[WEBHOOK] Notifying external systems about shipment {}", event.trackingId());
        // Simulate webhook call delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}