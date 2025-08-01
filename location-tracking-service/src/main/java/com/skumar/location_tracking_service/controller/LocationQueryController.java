package com.skumar.location_tracking_service.controller;

import com.skumar.location_tracking_service.dto.LocationUpdateDTO;
import com.skumar.location_tracking_service.service.LocationUpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestController
public class LocationQueryController {
    private final LocationUpdateService locationUpdateService;

    public LocationQueryController(LocationUpdateService locationUpdateService) {
        this.locationUpdateService = locationUpdateService;
    }

    public LocationUpdateService getLocationUpdateService() {
        return locationUpdateService;
    }

    @GetMapping("/api/driver/{driverId}/locations")
    public List<LocationUpdateDTO> getRecentLocations(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "10") int limit) {
        return locationUpdateService.getRecentLocations(driverId, limit);
    }

    @GetMapping("/api/drivers/locations")
    public List<LocationUpdateDTO> getLatestLocationsForAllDrivers() {
        return locationUpdateService.getLatestLocationsForAllDrivers();
    }

    @GetMapping("/api/drivers/locations/filter")
    public List<LocationUpdateDTO> getLatestLocationsForAllDriversByCityAndStatus(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status) {
        return locationUpdateService.getLatestLocationsForAllDriversByCityAndStatus(city, status);
    }

    @RestControllerAdvice
    public static class LocationQueryExceptionHandler {
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception ex) {
            return ResponseEntity.internalServerError().body("Error: " + ex.getMessage());
        }
    }
}
