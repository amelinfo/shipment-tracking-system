package com.logistics.tracking_service.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.logistics.tracking_service.entity.Shipment;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShipmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    void findByTrackingId_ExistingShipment_ReturnsShipment() {
        // Given
        Shipment shipment = Shipment.of("REPO_TEST", "IN_TRANSIT");
        entityManager.persistAndFlush(shipment);

        // When
        Optional<Shipment> found = shipmentRepository.findByTrackingId("REPO_TEST");

        // Then
        assertTrue(found.isPresent());
        assertEquals("REPO_TEST", found.get().trackingId());
        assertEquals("IN_TRANSIT", found.get().currentStatus());
    }

    @Test
    void findByTrackingId_NonExistent_ReturnsEmpty() {
        // When
        Optional<Shipment> found = shipmentRepository.findByTrackingId("NON_EXISTENT");

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void saveShipment_PersistsCorrectly() {
        // Given
        Shipment shipment = Shipment.of("SAVE_TEST", "CREATED");

        // When
        Shipment saved = shipmentRepository.save(shipment);

        // Then
        assertNotNull(saved.trackingId());
        assertEquals("SAVE_TEST", saved.trackingId());
        assertEquals("CREATED", saved.currentStatus());
        assertNotNull(saved.createdAt());
    }
}