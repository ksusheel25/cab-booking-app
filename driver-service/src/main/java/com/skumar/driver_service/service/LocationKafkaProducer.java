package com.skumar.driver_service.service;

import com.skumar.driver_service.dto.LocationUpdateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LocationKafkaProducer {
    private final KafkaTemplate<String, LocationUpdateDTO> kafkaTemplate;
    private final String topic;

    public LocationKafkaProducer(KafkaTemplate<String, LocationUpdateDTO> kafkaTemplate,
                                 @Value("${app.kafka.location-topic:driver-location-updates}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendLocationUpdate(LocationUpdateDTO locationUpdate) {
        kafkaTemplate.send(topic, locationUpdate);
    }
}
