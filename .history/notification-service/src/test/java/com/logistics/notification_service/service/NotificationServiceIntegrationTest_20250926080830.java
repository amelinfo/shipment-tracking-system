package com.logistics.notification_service.service;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092"
})
public class NotificationServiceIntegrationTest {

}
