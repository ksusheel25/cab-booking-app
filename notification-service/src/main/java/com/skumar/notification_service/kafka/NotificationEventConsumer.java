package com.skumar.notification_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skumar.notification_service.dto.NotificationEventDTO;
import com.skumar.notification_service.service.NotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationService notificationService;

    public NotificationEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    @KafkaListener(topics = "notification-events", groupId = "notification-service-group")
    public void consume(ConsumerRecord<String, String> record) {
        logger.info("Consuming notification event from Kafka: {}", record.value());
        try {
            NotificationEventDTO event = objectMapper.readValue(record.value(), NotificationEventDTO.class);
            logger.info("Parsed notification event: {}", event);
            notificationService.sendNotification(event);
        } catch (Exception e) {
            logger.error("Failed to parse notification event: {}", record.value(), e);
        }
    }
}
