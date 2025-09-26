package com.logistics.tracking_service.dto;

import java.time.LocalDateTime;

public record TrackingResult(
    String trackingId,
    String currentStatus,
    boolean statusChanged,
    String message,
    boolean success,
    LocalDateTime timestamp
) {
    
    public static TrackingResult success(String trackingId, String currentStatus, 
                                       boolean statusChanged, String message) {
        return new TrackingResult(trackingId, currentStatus, statusChanged, message, true, LocalDateTime.now());
    }
    
    public static TrackingResult error(String trackingId, String errorMessage) {
        return new TrackingResult(trackingId, null, false, errorMessage, false, LocalDateTime.now());
    }
    
    // Convenience static factory methods
    public static TrackingResult of(String trackingId, String currentStatus, boolean statusChanged) {
        String message = statusChanged ? 
            "Status changed to: " + currentStatus : 
            "Status unchanged: " + currentStatus;
        return success(trackingId, currentStatus, statusChanged, message);
    }
}