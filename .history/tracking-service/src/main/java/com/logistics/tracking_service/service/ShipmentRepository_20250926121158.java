package com.logistics.tracking_service.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logistics.tracking_service.entity.Shipment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    
    Optional<Shipment> findByTrackingId(String trackingId);
    
    List<Shipment> findByCurrentStatus(String status);
    
    @Query("SELECT s FROM Shipment s WHERE s.updatedAt >= :since")
    List<Shipment> findRecentlyUpdated(@Param("since") LocalDateTime since);
    
    @Query("SELECT s FROM Shipment s WHERE s.checkCount > :minChecks")
    List<Shipment> findFrequentlyChecked(@Param("minChecks") int minChecks);
    
    default Optional<Shipment> findAndUpdateCheck(String trackingId) {
        return findByTrackingId(trackingId)
            .map(shipment -> {
                // This would need a custom update method for production
                return shipment;
            });
    }
    
    // Using Java 21 features in default methods
    default List<Shipment> findShipmentsWithStatusChange() {
        return findAll().stream()
            .filter(Shipment::hasStatusChanged)
            .toList(); // New toList() method in Java 16+
    }
}