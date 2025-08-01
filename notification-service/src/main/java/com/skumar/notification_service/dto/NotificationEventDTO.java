package com.skumar.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEventDTO {
    private Long driverId;
    private String type; // e.g., "DRIVER_AVAILABLE", "GEOFENCE_ENTERED"
    private String message;
    private long timestamp;
}
