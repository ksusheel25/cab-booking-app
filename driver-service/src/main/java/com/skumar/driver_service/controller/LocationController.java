package com.skumar.driver_service.controller;

import com.skumar.driver_service.dto.LocationUpdateDTO;
import com.skumar.driver_service.service.LocationKafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    private final LocationKafkaProducer locationKafkaProducer;

    public LocationController(LocationKafkaProducer locationKafkaProducer) {
        this.locationKafkaProducer = locationKafkaProducer;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateDTO locationUpdateDTO) {
        locationKafkaProducer.sendLocationUpdate(locationUpdateDTO);
        return ResponseEntity.ok("Location update sent to Kafka");
    }
}
