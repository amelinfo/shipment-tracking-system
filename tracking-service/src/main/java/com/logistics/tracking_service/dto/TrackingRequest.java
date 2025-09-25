package com.logistics.tracking_service.dto;

import jakarta.validation.constraints.NotBlank;

public record TrackingRequest(
    @NotBlank(message = "Tracking ID is required")
    String trackingId
) {
    // Compact constructor for validation
    public TrackingRequest {
        if (trackingId != null) {
            trackingId = trackingId.trim();
        }
    }
    
    // Static factory method for convenience
    public static TrackingRequest of(String trackingId) {
        return new TrackingRequest(trackingId);
    }
}