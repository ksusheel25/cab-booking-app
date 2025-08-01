package com.skumar.location_tracking_service.service;

import com.skumar.location_tracking_service.dto.LocationUpdateDTO;
import com.skumar.location_tracking_service.dto.NotificationEventDTO;
import com.skumar.location_tracking_service.entity.LocationUpdate;
import com.skumar.location_tracking_service.kafka.NotificationEventProducer;
import com.skumar.location_tracking_service.repository.LocationUpdateRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateService.class);
    private final LocationUpdateRepository repository;
    private final NotificationEventProducer notificationEventProducer;

    public LocationUpdateService(LocationUpdateRepository repository, NotificationEventProducer notificationEventProducer) {
        this.repository = repository;
        this.notificationEventProducer = notificationEventProducer;
    }

    public void saveLocation(LocationUpdateDTO dto) {
        logger.info("Saving location update for driverId={}, city={}, status={}", dto.getDriverId(), dto.getCity(), dto.getStatus());
        LocationUpdate entity = new LocationUpdate();
        entity.setDriverId(dto.getDriverId());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setTimestamp(dto.getTimestamp());
        entity.setCity(dto.getCity());
        entity.setStatus(dto.getStatus());
        repository.save(entity);
        logger.info("Location update saved for driverId={}", dto.getDriverId());
        if ("AVAILABLE".equalsIgnoreCase(dto.getStatus())) {
            NotificationEventDTO event = new NotificationEventDTO(
                dto.getDriverId(),
                "DRIVER_AVAILABLE",
                "Driver " + dto.getDriverId() + " is now available in " + dto.getCity(),
                System.currentTimeMillis()
            );
            logger.info("Sending notification event for driverId={}: {}", dto.getDriverId(), event);
            notificationEventProducer.sendNotification(event);
        }
    }

    public List<LocationUpdateDTO> getRecentLocations(Long driverId, int limit) {
        logger.info("Fetching recent {} locations for driverId={}", limit, driverId);
        List<LocationUpdateDTO> result = repository.findAll().stream()
                .filter(l -> l.getDriverId().equals(driverId))
                .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
                .limit(limit)
                .map(l -> new LocationUpdateDTO(l.getDriverId(), l.getLatitude(), l.getLongitude(), l.getTimestamp()))
                .collect(Collectors.toList());
        logger.info("Found {} recent locations for driverId={}", result.size(), driverId);
        return result;
    }

    public List<LocationUpdateDTO> getLatestLocationsForAllDrivers() {
        logger.info("Fetching latest locations for all drivers");
        List<LocationUpdateDTO> result = repository.findAll().stream()
                .collect(Collectors.groupingBy(LocationUpdate::getDriverId))
                .values().stream()
                .map(list -> list.stream().max((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp())).get())
                .map(l -> new LocationUpdateDTO(l.getDriverId(), l.getLatitude(), l.getLongitude(), l.getTimestamp()))
                .collect(Collectors.toList());
        logger.info("Found latest locations for {} drivers", result.size());
        return result;
    }

    public List<LocationUpdateDTO> getLatestLocationsForAllDriversByCityAndStatus(String city, String status) {
        logger.info("Fetching latest locations for all drivers by city='{}' and status='{}'", city, status);
        List<LocationUpdateDTO> result = repository.findAll().stream()
                .filter(l -> (city == null || city.equalsIgnoreCase(l.getCity())))
                .filter(l -> (status == null || status.equalsIgnoreCase(l.getStatus())))
                .collect(Collectors.groupingBy(LocationUpdate::getDriverId))
                .values().stream()
                .map(list -> list.stream().max((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp())).get())
                .map(l -> new LocationUpdateDTO(l.getDriverId(), l.getLatitude(), l.getLongitude(), l.getTimestamp(), l.getCity(), l.getStatus()))
                .collect(Collectors.toList());
        logger.info("Found latest locations for {} drivers with city='{}' and status='{}'", result.size(), city, status);
        return result;
    }
}
