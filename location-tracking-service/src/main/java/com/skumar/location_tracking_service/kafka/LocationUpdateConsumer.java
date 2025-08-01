package com.skumar.location_tracking_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skumar.location_tracking_service.dto.LocationUpdateDTO;
import com.skumar.location_tracking_service.service.LocationUpdateService;
import com.skumar.location_tracking_service.websocket.LocationWebSocketPublisher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LocationUpdateConsumer {
    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LocationUpdateService locationUpdateService;
    private final LocationWebSocketPublisher webSocketPublisher;

    public LocationUpdateConsumer(LocationUpdateService locationUpdateService, LocationWebSocketPublisher webSocketPublisher) {
        this.locationUpdateService = locationUpdateService;
        this.webSocketPublisher = webSocketPublisher;
    }

    public LocationUpdateService getLocationUpdateService() {
        return locationUpdateService;
    }

    public LocationWebSocketPublisher getWebSocketPublisher() {
        return webSocketPublisher;
    }

    @KafkaListener(topics = "driver-location-updates", groupId = "location-tracking-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            LocationUpdateDTO location = objectMapper.readValue(record.value(), LocationUpdateDTO.class);
            logger.info("Received location update: {}", location);
            locationUpdateService.saveLocation(location);
            webSocketPublisher.broadcastLocation(location);
        } catch (Exception e) {
            logger.error("Failed to parse location update: {}", record.value(), e);
        }
    }
}
