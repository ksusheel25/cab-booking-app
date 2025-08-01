package com.skumar.driver_service.service.impl;

import com.skumar.driver_service.dto.DriverDTO;
import com.skumar.driver_service.dto.DriverRegistrationDTO;
import com.skumar.driver_service.entity.Driver;
import com.skumar.driver_service.entity.DriverStatus;
import com.skumar.driver_service.entity.Role;
import com.skumar.driver_service.exception.ApiException;
import com.skumar.driver_service.repository.DriverRepository;
import com.skumar.driver_service.service.DriverService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public DriverServiceImpl(DriverRepository driverRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public DriverRepository getDriverRepository() {
        return driverRepository;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Override
    public DriverDTO registerDriver(DriverRegistrationDTO registrationDTO) {
        if (driverRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new ApiException("Email already registered", HttpStatus.BAD_REQUEST);
        }
        if (driverRepository.existsByLicenseNumber(registrationDTO.getLicenseNumber())) {
            throw new ApiException("License number already registered", HttpStatus.BAD_REQUEST);
        }

        Driver driver = new Driver();
        driver.setName(registrationDTO.getName());
        driver.setEmail(registrationDTO.getEmail());
        driver.setPhone(registrationDTO.getPhone());
        driver.setLicenseNumber(registrationDTO.getLicenseNumber());
        driver.setAddress(registrationDTO.getAddress());
        driver.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        driver.setRole(Role.ROLE_DRIVER);
        driver.setStatus(DriverStatus.PENDING);

        return mapToDTO(driverRepository.save(driver));
    }

    @Override
    public DriverDTO getDriverById(Long id) {
        return mapToDTO(findDriverById(id));
    }

    @Override
    public DriverDTO getDriverByEmail(String email) {
        return driverRepository.findByEmail(email)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ApiException("Driver not found with email: " + email, HttpStatus.NOT_FOUND));
    }

    @Override
    public DriverDTO updateDriver(Long id, DriverDTO driverDTO) {
        Driver driver = findDriverById(id);
        
        if (!driver.getEmail().equals(driverDTO.getEmail()) && 
            driverRepository.existsByEmail(driverDTO.getEmail())) {
            throw new ApiException("Email already in use", HttpStatus.BAD_REQUEST);
        }

        driver.setName(driverDTO.getName());
        driver.setEmail(driverDTO.getEmail());
        driver.setPhone(driverDTO.getPhone());
        driver.setAddress(driverDTO.getAddress());

        return mapToDTO(driverRepository.save(driver));
    }

    @Override
    public Page<DriverDTO> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public List<DriverDTO> searchDrivers(String query) {
        return driverRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DriverDTO updateDriverStatus(Long id, DriverStatus status) {
        Driver driver = findDriverById(id);
        driver.setStatus(status);
        return mapToDTO(driverRepository.save(driver));
    }

    @Override
    public DriverDTO updateDriverAvailability(Long id, boolean isAvailable) {
        Driver driver = findDriverById(id);
        if (driver.getStatus() != DriverStatus.APPROVED) {
            throw new ApiException("Only approved drivers can update availability", HttpStatus.BAD_REQUEST);
        }
        driver.setAvailable(isAvailable);
        return mapToDTO(driverRepository.save(driver));
    }

    @Override
    public DriverDTO updateDriverLocation(Long id, Double lat, Double lng) {
        Driver driver = findDriverById(id);
        driver.setLocationLat(lat);
        driver.setLocationLng(lng);
        return mapToDTO(driverRepository.save(driver));
    }

    @Override
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ApiException("Driver not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        driverRepository.deleteById(id);
    }

    @Override
    public Map<String, Long> getDriverStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", driverRepository.count());
        stats.put("pending", driverRepository.countByStatus(DriverStatus.PENDING));
        stats.put("approved", driverRepository.countByStatus(DriverStatus.APPROVED));
        stats.put("blocked", driverRepository.countByStatus(DriverStatus.BLOCKED));
        stats.put("available", driverRepository.countAvailableDrivers());
        return stats;
    }

    @Override
    public DriverDTO updateDriverRole(Long id, String role) {
        Driver driver = findDriverById(id);
        try {
            driver.setRole(Role.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid role: " + role, HttpStatus.BAD_REQUEST);
        }
        return mapToDTO(driverRepository.save(driver));
    }

    @Override
    public List<DriverDTO> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean verifyDriver(Long id) {
        Driver driver = findDriverById(id);
        if (driver.getStatus() != DriverStatus.PENDING) {
            throw new ApiException("Driver is not in PENDING status", HttpStatus.BAD_REQUEST);
        }
        driver.setStatus(DriverStatus.APPROVED);
        driverRepository.save(driver);
        return true;
    }

    private Driver findDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ApiException("Driver not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private DriverDTO mapToDTO(Driver driver) {
        return modelMapper.map(driver, DriverDTO.class);
    }
}