package com.skumar.location_tracking_service.websocket;

import com.skumar.location_tracking_service.dto.LocationUpdateDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LocationWebSocketPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public LocationWebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public SimpMessagingTemplate getMessagingTemplate() {
        return messagingTemplate;
    }

    public void broadcastLocation(LocationUpdateDTO locationUpdateDTO) {
        messagingTemplate.convertAndSend("/topic/location-updates", locationUpdateDTO);
    }
}
