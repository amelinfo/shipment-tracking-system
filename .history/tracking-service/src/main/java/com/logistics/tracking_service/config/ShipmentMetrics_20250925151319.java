package com.logistics.tracking_service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ShipmentMetrics {
    
    private final Counter statusChangeCounter;
    private final Timer externalApiTimer;

    public ShipmentMetrics(MeterRegistry registry) {
        this.statusChangeCounter = Counter.builder("shipment.status.changes")
                .description("Total number of shipment status change events")
                .register(registry);
                
        this.externalApiTimer = Timer.builder("external.api.latency")
                .description("Latency of external API calls for shipment status")
                .register(registry);
    }

    public void incrementStatusChangeCounter() {
        statusChangeCounter.increment();
    }

    public Timer.Sample startApiTimer() {
        return Timer.start();
    }

    public void stopApiTimer(Timer.Sample sample) {
        sample.stop(externalApiTimer);
    }

    public void recordApiLatency(long duration, TimeUnit unit) {
        externalApiTimer.record(duration, unit);
    }
}