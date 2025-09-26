package com.logistics.tracking_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    private String trackingId;
    
    @Column(nullable = false)
    private String currentStatus;
    
    @Column(name = "previous_status")
    private String previousStatus;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "last_checked")
    private LocalDateTime lastChecked;
    
    @Column(name = "check_count", nullable = false)
    private Integer checkCount;
    
    @Column(columnDefinition = "TEXT")
    private String metadata;

    // Default constructor for JPA
    protected Shipment() {}

    // Constructor
    public Shipment(String trackingId, String currentStatus, String previousStatus, 
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastChecked, 
                   Integer checkCount, String metadata) {
        if (trackingId == null || trackingId.isBlank()) {
            throw new IllegalArgumentException("Tracking ID cannot be null or blank");
        }
        if (currentStatus == null || currentStatus.isBlank()) {
            throw new IllegalArgumentException("Current status cannot be null or blank");
        }
        
        this.trackingId = trackingId;
        this.currentStatus = currentStatus;
        this.previousStatus = previousStatus;
        this.createdAt = Optional.ofNullable(createdAt).orElse(LocalDateTime.now());
        this.updatedAt = Optional.ofNullable(updatedAt).orElse(LocalDateTime.now());
        this.lastChecked = Optional.ofNullable(lastChecked).orElse(LocalDateTime.now());
        this.checkCount = Optional.ofNullable(checkCount).orElse(1);
        this.metadata = Optional.ofNullable(metadata).orElse("{}");
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

    // Getters
    public String trackingId() { return trackingId; }
    public String currentStatus() { return currentStatus; }
    public String previousStatus() { return previousStatus; }
    public LocalDateTime createdAt() { return createdAt; }
    public LocalDateTime updatedAt() { return updatedAt; }
    public LocalDateTime lastChecked() { return lastChecked; }
    public Integer checkCount() { return checkCount; }
    public String metadata() { return metadata; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return Objects.equals(trackingId, shipment.trackingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingId);
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "trackingId='" + trackingId + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", previousStatus='" + previousStatus + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastChecked=" + lastChecked +
                ", checkCount=" + checkCount +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}