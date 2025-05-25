package com.skumar.driver_service.repository;

import com.skumar.driver_service.entity.Driver;
import com.skumar.driver_service.entity.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    
    List<Driver> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    
    List<Driver> findByStatus(DriverStatus status);
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.status = ?1")
    long countByStatus(DriverStatus status);
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.isAvailable = true AND d.status = 'APPROVED'")
    long countAvailableDrivers();
    
    boolean existsByEmail(String email);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.status = 'APPROVED'")
    List<Driver> findAvailableDrivers();
}