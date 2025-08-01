package com.skumar.driver_service.dto;

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
}
