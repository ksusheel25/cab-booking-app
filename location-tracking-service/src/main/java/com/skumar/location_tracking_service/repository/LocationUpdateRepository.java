package com.skumar.location_tracking_service.repository;

import com.skumar.location_tracking_service.entity.LocationUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationUpdateRepository extends JpaRepository<LocationUpdate, Long> {
    // Additional query methods can be defined here
}
