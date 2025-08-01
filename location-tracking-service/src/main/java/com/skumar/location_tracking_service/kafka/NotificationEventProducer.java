package com.skumar.location_tracking_service.kafka;

import com.skumar.location_tracking_service.dto.NotificationEventDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationEventProducer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventProducer.class);
    private final KafkaTemplate<String, NotificationEventDTO> kafkaTemplate;
    private final String topic;

    public NotificationEventProducer(KafkaTemplate<String, NotificationEventDTO> kafkaTemplate,
                                     @Value("${app.kafka.notification-topic:notification-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public KafkaTemplate<String, NotificationEventDTO> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public String getTopic() {
        return topic;
    }

    public void sendNotification(NotificationEventDTO event) {
        logger.info("Producing notification event to Kafka: {}", event);
        kafkaTemplate.send(topic, event);
    }
}
