package com.skumar.driver_service.dto;

import com.skumar.driver_service.entity.DriverStatus;
import com.skumar.driver_service.entity.Role;
import java.util.List;

public class DriverDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private Role role;
    private DriverStatus status;
    private List<DocumentDTO> documents;
    private String address;
    private boolean isAvailable;
    private Double locationLat;
    private Double locationLng;

    public DriverDTO() {
    }

    public DriverDTO(Long id, String name, String email, String phone, 
                    String licenseNumber, Role role, DriverStatus status, 
                    List<DocumentDTO> documents, String address, boolean isAvailable, 
                    Double locationLat, Double locationLng) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.role = role;
        this.status = status;
        this.documents = documents;
        this.address = address;
        this.isAvailable = isAvailable;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public Double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(Double locationLng) {
        this.locationLng = locationLng;
    }
}