package com.logistics.tracking_service.service;

import org.springframework.stereotype.Service;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Service
public class ExternalShipmentService {
    
    private final RandomGenerator random = RandomGeneratorFactory.of("L32X64MixRandom").create();
    
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
        int value = random.nextInt(100); // 0-99
        
        return switch (value) {
            case 0, 1, 2, 3, 4 -> "CREATED";           
            case 5, 6, 7, 8, 9 -> "PICKED_UP";         
            default -> {
                if (value >= 10 && value <= 49) yield "IN_TRANSIT";        
                else if (value >= 50 && value <= 69) yield "OUT_FOR_DELIVERY"; 
                else if (value >= 70 && value <= 94) yield "DELIVERED";        // 25% chance
                else yield "EXCEPTION";                                         // 5% chance (95-99)
            }
        };
    }
    
    public String getApiEndpoint(String trackingId) {
        return """
            https://api.logistics.com/v1/shipments/%s/status
            """.formatted(trackingId);
    }
}