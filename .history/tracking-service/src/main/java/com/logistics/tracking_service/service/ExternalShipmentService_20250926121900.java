package com.logistics.tracking_service.service;

import org.springframework.stereotype.Service;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Service
public class ExternalShipmentService {
    
    private final RandomGenerator random = RandomGeneratorFactory.of("L32X64MixRandom").create();
    
    private final java.util.List<String> possibleStatuses = java.util.List.of(
        "CREATED", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED", "EXCEPTION"
    );
    
    public String getShipmentStatus(String trackingId) {
        try {
            Thread.sleep(100 + random.nextInt(400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("API call interrupted", e);
        }
        
        return getWeightedRandomStatus();
    }
    
    private String getWeightedRandomStatus() {
        double weight = random.nextDouble();

        int value = (int) (weight * 100);
        if (value >= 0 && value <= 4) return "CREATED";
        else if (value >= 5 && value <= 9) return "PICKED_UP";
        else if (value >= 10 && value <= 49) return "IN_TRANSIT";
        else if (value >= 50 && value <= 69) return "OUT_FOR_DELIVERY";
        else if (value >= 70 && value <= 94) return "DELIVERED";
        else if (value >= 95 && value <= 99) return "EXCEPTION";
        else return "IN_TRANSIT";
    }
    
    public String getApiEndpoint(String trackingId) {
        return """
            https://api.logistics.com/v1/shipments/%s/status
            """.formatted(trackingId);
    }
}