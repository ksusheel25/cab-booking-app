package com.skumar.location_tracking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdateDTO {
    private Long driverId;
    private double latitude;
    private double longitude;
    private long timestamp;
    private String city;
    private String status;
}
