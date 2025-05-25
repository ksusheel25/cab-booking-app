package com.skumar.driver_service.dto;

import com.skumar.driver_service.entity.DocumentType;
import com.skumar.driver_service.entity.VerificationStatus;

public class DocumentDTO {
    private Long id;
    private DocumentType type;
    private String fileName;
    private String fileUrl;
    private VerificationStatus verificationStatus;
    private Long driverId;

    public DocumentDTO() {
    }

    public DocumentDTO(Long id, DocumentType type, String fileName, String fileUrl,
                      VerificationStatus verificationStatus, Long driverId) {
        this.id = id;
        this.type = type;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.verificationStatus = verificationStatus;
        this.driverId = driverId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}