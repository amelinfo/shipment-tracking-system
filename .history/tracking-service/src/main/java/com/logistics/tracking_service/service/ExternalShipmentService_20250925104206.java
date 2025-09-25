package com.logistics.tracking_service.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class ExternalShipmentService {
    
    private final List<String> possibleStatuses = Arrays.asList(
        "CREATED", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED", "EXCEPTION"
    );
    
    private final Random random = new Random();

    /**
     * Mock external API call to get shipment status
     * Simulates calling an external logistics API
     */
    public String getShipmentStatus(String trackingId) {
        // Simulate API latency (100-500ms)
        try {
            Thread.sleep(100 + random.nextInt(400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Return random status for demo purposes
        // In real implementation, this would call an actual external API
        return possibleStatuses.get(random.nextInt(possibleStatuses.size()));
    }
}