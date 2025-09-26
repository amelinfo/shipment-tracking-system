package com.logistics.tracking_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalShipmentServiceTest {

    private final ExternalShipmentService service = new ExternalShipmentService();

    @Test
    void getShipmentStatus_ReturnsValidStatus() {
        // When
        String status = service.getShipmentStatus("TEST123");

        // Then
        assertNotNull(status);
        assertTrue(status.matches("CREATED|PICKED_UP|IN_TRANSIT|OUT_FOR_DELIVERY|DELIVERED|EXCEPTION"));
    }

    @Test
    void getShipmentStatus_DifferentTrackingIds_ReturnStatus() {
        // When
        String status1 = service.getShipmentStatus("TRACK001");
        String status2 = service.getShipmentStatus("TRACK002");

        // Then
        assertNotNull(status1);
        assertNotNull(status2);
        // They might be different due to random generation
    }
}