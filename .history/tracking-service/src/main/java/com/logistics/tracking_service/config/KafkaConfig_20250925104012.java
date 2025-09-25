package com.logistics.tracking_service.config;

@Configuration
public class KafkaConfig {
    
    @Bean
    public ProducerFactory<String, ShipmentEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Important: Configure Jackson for record support
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, 
            "shipmentEvent:com.logistics.tracking.dto.ShipmentEvent");
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}