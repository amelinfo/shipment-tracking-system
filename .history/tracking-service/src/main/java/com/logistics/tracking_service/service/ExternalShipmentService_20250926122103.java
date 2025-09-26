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
        
        return switch (value) {
            case 0, 1, 2, 3, 4 -> "CREATED";
            case 5, 6, 7, 8, 9 -> "PICKED_UP";
            default -> {
                if (value >= 10 && value <= 49) yield "IN_TRANSIT";
                else if (value >= 50 && value <= 69) yield "OUT_FOR_DELIVERY";
                else if (value >= 70 && value <= 94) yield "DELIVERED";
                else if (value >= 95 && value <= 99) yield "EXCEPTION";
                else yield "IN_TRANSIT";
            }
        };
    }
    
    public String getApiEndpoint(String trackingId) {
        return """
            https://api.logistics.com/v1/shipments/%s/status
            """.formatted(trackingId);
    }
}