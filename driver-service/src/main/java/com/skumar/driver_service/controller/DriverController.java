package com.skumar.driver_service.controller;

import com.skumar.driver_service.dto.DriverDTO;
import com.skumar.driver_service.dto.DriverRegistrationDTO;
import com.skumar.driver_service.dto.LoginRequestDTO;
import com.skumar.driver_service.entity.DocumentType;
import com.skumar.driver_service.entity.Driver;
import com.skumar.driver_service.service.DocumentService;
import com.skumar.driver_service.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/drivers")
@Tag(name = "Driver", description = "Driver management APIs")
public class DriverController {

    private final DriverService driverService;
    private final DocumentService documentService;

    public DriverController(DriverService driverService, DocumentService documentService) {
        this.driverService = driverService;
        this.documentService = documentService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new driver")
    public ResponseEntity<DriverDTO> registerDriver(@Valid @RequestBody DriverRegistrationDTO registrationDTO) {
        return new ResponseEntity<>(driverService.registerDriver(registrationDTO), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current driver profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DriverDTO> getCurrentDriver(@AuthenticationPrincipal Driver driver) {
        return ResponseEntity.ok(driverService.getDriverByEmail(driver.getEmail()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current driver profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DriverDTO> updateProfile(
            @AuthenticationPrincipal Driver driver,
            @Valid @RequestBody DriverDTO driverDTO) {
        return ResponseEntity.ok(driverService.updateDriver(driver.getId(), driverDTO));
    }

    @PostMapping("/me/documents")
    @Operation(summary = "Upload driver document", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> uploadDocument(
            @AuthenticationPrincipal Driver driver,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file) {
        return ResponseEntity.ok(documentService.uploadDocument(driver.getId(), documentType, file));
    }

    @GetMapping("/me/documents")
    @Operation(summary = "Get driver documents", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getDocuments(@AuthenticationPrincipal Driver driver) {
        return ResponseEntity.ok(documentService.getDriverDocuments(driver.getId()));
    }

    @PatchMapping("/me/availability")
    @Operation(summary = "Update driver availability", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DriverDTO> updateAvailability(
            @AuthenticationPrincipal Driver driver,
            @RequestParam boolean available) {
        return ResponseEntity.ok(driverService.updateDriverAvailability(driver.getId(), available));
    }

    @PatchMapping("/me/location")
    @Operation(summary = "Update driver location", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DriverDTO> updateLocation(
            @AuthenticationPrincipal Driver driver,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        return ResponseEntity.ok(driverService.updateDriverLocation(driver.getId(), lat, lng));
    }

    @PostMapping("/login")
    @Operation(summary = "Login driver")
    public ResponseEntity<?> loginDriver(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(Map.of(
            "message", "Please use the gateway service for authentication",
            "email", loginRequest.getEmail()
        ));
    }
}