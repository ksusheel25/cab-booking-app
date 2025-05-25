package com.skumar.driver_service.service;

import com.skumar.driver_service.dto.DriverDTO;
import com.skumar.driver_service.dto.DriverRegistrationDTO;
import com.skumar.driver_service.entity.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DriverService {
    DriverDTO registerDriver(DriverRegistrationDTO registrationDTO);
    
    DriverDTO getDriverById(Long id);
    
    DriverDTO getDriverByEmail(String email);
    
    DriverDTO updateDriver(Long id, DriverDTO driverDTO);
    
    Page<DriverDTO> getAllDrivers(Pageable pageable);
    
    List<DriverDTO> searchDrivers(String query);
    
    DriverDTO updateDriverStatus(Long id, DriverStatus status);
    
    DriverDTO updateDriverAvailability(Long id, boolean isAvailable);
    
    DriverDTO updateDriverLocation(Long id, Double lat, Double lng);
    
    void deleteDriver(Long id);
    
    Map<String, Long> getDriverStatistics();
    
    DriverDTO updateDriverRole(Long id, String role);
    
    List<DriverDTO> getAvailableDrivers();
    
    boolean verifyDriver(Long id);
}