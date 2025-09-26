package com.logistics.notification_service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {
    
    private final Counter notificationsProcessedCounter;
    private final Counter notificationErrorsCounter;

    public NotificationMetrics(MeterRegistry registry) {
        this.notificationsProcessedCounter = Counter.builder("notification.events.processed")
                .description("Total number of notification events processed successfully")
                .register(registry);
                
        this.notificationErrorsCounter = Counter.builder("notification.errors")
                .description("Total number of notification processing errors")
                .register(registry);
    }

    public void incrementProcessedCounter() {
        notificationsProcessedCounter.increment();
    }

    public void incrementErrorCounter() {
        notificationErrorsCounter.increment();
    }
}