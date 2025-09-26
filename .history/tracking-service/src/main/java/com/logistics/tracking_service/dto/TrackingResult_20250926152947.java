package com.logistics.tracking_service.dto;

import java.time.LocalDateTime;

@Schema(description = "Response object containing tracking result and status information")
public record TrackingResult(
    @Schema(description = "Tracking ID of the shipment", example = "ABC123XYZ")
    String trackingId,
    
    @Schema(
        description = "Current status of the shipment",
        example = "IN_TRANSIT",
        allowableValues = {"CREATED", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED", "EXCEPTION"}
    )
    String currentStatus,
    
    @Schema(description = "Indicates if the status changed from previous check", example = "true")
    boolean statusChanged,
    
    @Schema(description = "Descriptive message about the tracking result", example = "Status changed from CREATED to IN_TRANSIT")
    String message,
    
    @Schema(description = "Indicates if the tracking request was processed successfully", example = "true")
    boolean success,
    
    @Schema(description = "Timestamp when the tracking was processed", example = "2024-01-15T10:30:00")
    LocalDateTime timestamp
) {
    
    public static TrackingResult success(String trackingId, String currentStatus, 
                                       boolean statusChanged, String message) {
        return new TrackingResult(trackingId, currentStatus, statusChanged, message, true, LocalDateTime.now());
    }
    
    public static TrackingResult error(String trackingId, String errorMessage) {
        return new TrackingResult(trackingId, null, false, errorMessage, false, LocalDateTime.now());
    }
    
    public static TrackingResult of(String trackingId, String currentStatus, boolean statusChanged) {
        String message = statusChanged ? 
            "Status changed to: " + currentStatus : 
            "Status unchanged: " + currentStatus;
        return success(trackingId, currentStatus, statusChanged, message);
    }

    public static TrackingResult statusChanged(String trackingId, String oldStatus, String newStatus) {
        return success(trackingId, newStatus, true, 
            "Status changed from '" + oldStatus + "' to '" + newStatus + "'");
    }
    
    public static TrackingResult statusUnchanged(String trackingId, String status) {
        return success(trackingId, status, false, "Status unchanged: " + status);
    }
    
    public static TrackingResult initialStatus(String trackingId, String status) {
        return success(trackingId, status, true, "Initial status: " + status);
    }
}