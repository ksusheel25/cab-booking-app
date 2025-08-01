package com.skumar.notification_service.service;

import com.skumar.notification_service.dto.NotificationEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendNotification(NotificationEventDTO event) {
        logger.info("Sending notification: {}", event);
        // For demo: just log the notification. Replace with email/SMS/push logic as needed.
    }
}
