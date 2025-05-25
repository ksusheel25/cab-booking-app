package com.skumar.driver_service.controller;

import com.skumar.driver_service.dto.DriverDTO;
import com.skumar.driver_service.entity.DocumentType;
import com.skumar.driver_service.entity.DriverStatus;
import com.skumar.driver_service.service.DocumentService;
import com.skumar.driver_service.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final DriverService driverService;
    private final DocumentService documentService;

    public AdminController(DriverService driverService, DocumentService documentService) {
        this.driverService = driverService;
        this.documentService = documentService;
    }

    @GetMapping("/drivers")
    @Operation(summary = "Get all drivers with pagination")
    public ResponseEntity<Page<DriverDTO>> getAllDrivers(Pageable pageable) {
        return ResponseEntity.ok(driverService.getAllDrivers(pageable));
    }

    @GetMapping("/drivers/search")
    @Operation(summary = "Search drivers by name or email")
    public ResponseEntity<?> searchDrivers(@RequestParam String query) {
        return ResponseEntity.ok(driverService.searchDrivers(query));
    }

    @GetMapping("/drivers/{id}")
    @Operation(summary = "Get driver by ID")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PatchMapping("/drivers/{id}/status")
    @Operation(summary = "Update driver status")
    public ResponseEntity<DriverDTO> updateDriverStatus(
            @PathVariable Long id,
            @RequestParam DriverStatus status) {
        return ResponseEntity.ok(driverService.updateDriverStatus(id, status));
    }

    @PatchMapping("/drivers/{id}/role")
    @Operation(summary = "Update driver role")
    public ResponseEntity<DriverDTO> updateDriverRole(
            @PathVariable Long id,
            @RequestParam String role) {
        return ResponseEntity.ok(driverService.updateDriverRole(id, role));
    }

    @GetMapping("/drivers/statistics")
    @Operation(summary = "Get driver statistics")
    public ResponseEntity<?> getDriverStatistics() {
        return ResponseEntity.ok(driverService.getDriverStatistics());
    }

    @GetMapping("/drivers/{driverId}/documents")
    @Operation(summary = "Get driver documents")
    public ResponseEntity<?> getDriverDocuments(@PathVariable Long driverId) {
        return ResponseEntity.ok(documentService.getDriverDocuments(driverId));
    }

    @PostMapping("/drivers/documents/{documentId}/verify")
    @Operation(summary = "Verify driver document")
    public ResponseEntity<?> verifyDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(documentService.verifyDocument(documentId));
    }

    @GetMapping("/drivers/available")
    @Operation(summary = "Get all available drivers")
    public ResponseEntity<?> getAvailableDrivers() {
        return ResponseEntity.ok(driverService.getAvailableDrivers());
    }

    @DeleteMapping("/drivers/{id}")
    @Operation(summary = "Delete driver")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }
}