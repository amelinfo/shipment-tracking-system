package com.logistics.tracking_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for tracking a shipment")
public record TrackingRequest(
    @Schema(
        description = "Unique tracking identifier provided by the logistics company",
        example = "ABC123XYZ",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 3,
        maxLength = 50
    )
    @NotBlank(message = "Tracking ID is required")
    String trackingId
) {
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