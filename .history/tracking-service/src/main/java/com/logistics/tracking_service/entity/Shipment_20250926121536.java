package com.logistics.tracking_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "shipments")
public record Shipment(
    @Id
    String trackingId,
    
    @Column(nullable = false)
    String currentStatus,
    
    @Column(name = "previous_status")
    String previousStatus,
    
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt,
    
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt,
    
    @Column(name = "last_checked")
    LocalDateTime lastChecked,
    
    @Column(name = "check_count", nullable = false)
    Integer checkCount,
    
    @Column(columnDefinition = "TEXT")
    String metadata
) {
    
    public Shipment {
        if (trackingId == null || trackingId.isBlank()) {
            throw new IllegalArgumentException("Tracking ID cannot be null or blank");
        }
        if (currentStatus == null || currentStatus.isBlank()) {
            throw new IllegalArgumentException("Current status cannot be null or blank");
        }
        
        previousStatus = Optional.ofNullable(previousStatus).orElse(null);
        createdAt = Optional.ofNullable(createdAt).orElse(LocalDateTime.now());
        updatedAt = Optional.ofNullable(updatedAt).orElse(LocalDateTime.now());
        lastChecked = Optional.ofNullable(lastChecked).orElse(LocalDateTime.now());
        checkCount = Optional.ofNullable(checkCount).orElse(1);
        metadata = Optional.ofNullable(metadata).orElse("{}");
    }
    
    public static Shipment of(String trackingId, String currentStatus) {
        return new Shipment(
            trackingId, 
            currentStatus, 
            null, 
            LocalDateTime.now(), 
            LocalDateTime.now(), 
            LocalDateTime.now(), 
            1, 
            "{}"
        );
    }
    
    public static Shipment withStatusChange(String trackingId, String oldStatus, String newStatus) {
        return new Shipment(
            trackingId, 
            newStatus, 
            oldStatus, 
            LocalDateTime.now(), 
            LocalDateTime.now(), 
            LocalDateTime.now(), 
            1, 
            "{}"
        );
    }
    
    public Shipment withStatusUpdate(String newStatus) {
        return new Shipment(
            trackingId,
            newStatus,
            currentStatus,
            createdAt,
            LocalDateTime.now(),
            LocalDateTime.now(),
            checkCount + 1,
            metadata
        );
    }
    
    public boolean hasStatusChanged() {
        return previousStatus != null && !previousStatus.equals(currentStatus);
    }
    
    public Optional<String> getPreviousStatus() {
        return Optional.ofNullable(previousStatus);
    }
    
    public String getStatusCategory() {
        return switch (currentStatus) {
            case "CREATED", "PICKED_UP" -> "PREPARING";
            case "IN_TRANSIT", "OUT_FOR_DELIVERY" -> "IN_PROGRESS";
            case "DELIVERED" -> "COMPLETED";
            case "EXCEPTION", "RETURNED" -> "ISSUE";
            default -> "UNKNOWN";
        };
    }
}