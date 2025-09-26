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
        
        return switch ((int) (weight * 100)) {
            case 0, 1, 2, 3, 4 -> "CREATED";
            case 5, 6, 7, 8, 9 -> "PICKED_UP";
            case 10 to 49 -> "IN_TRANSIT";
            case 50 to 69 -> "OUT_FOR_DELIVERY";
            case 70 to 94 -> "DELIVERED";
            case 95 to 99 -> "EXCEPTION";
            default -> "IN_TRANSIT";
        };
    }
    
    public String getApiEndpoint(String trackingId) {
        return """
            https://api.logistics.com/v1/shipments/%s/status
            """.formatted(trackingId);
    }
}