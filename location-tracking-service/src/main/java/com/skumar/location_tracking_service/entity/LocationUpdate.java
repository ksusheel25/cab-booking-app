package com.skumar.location_tracking_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_updates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long driverId;
    private double latitude;
    private double longitude;
    private long timestamp;
    private String city;
    private String status; // e.g., AVAILABLE, BUSY, OFFLINE
}
