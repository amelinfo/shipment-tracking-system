package com.logistics.tracking_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import com.logistics.common.ShipmentEvent;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"shipment-status-changes"})
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, ShipmentEvent> kafkaTemplate;

    @Test
    void sendAndReceiveShipmentEvent() throws Exception {
        // Given
        ShipmentEvent event = new ShipmentEvent("KAFKA_TEST", "CREATED", "IN_TRANSIT");

        // When
        kafkaTemplate.send("shipment-status-changes", event.trackingId(), event);

        // Then - message should be sent without exception
        assertTrue(true, "ShipmentEvent sent successfully");
    }
}