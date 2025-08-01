package com.skumar.driver_service.service.impl;

import com.skumar.driver_service.config.StorageConfig;
import com.skumar.driver_service.entity.Document;
import com.skumar.driver_service.entity.DocumentType;
import com.skumar.driver_service.entity.Driver;
import com.skumar.driver_service.entity.VerificationStatus;
import com.skumar.driver_service.exception.DocumentException;
import com.skumar.driver_service.repository.DocumentRepository;
import com.skumar.driver_service.repository.DriverRepository;
import com.skumar.driver_service.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DriverRepository driverRepository;
    private final StorageConfig storageConfig;

    public DocumentServiceImpl(DocumentRepository documentRepository, DriverRepository driverRepository, StorageConfig storageConfig) {
        this.documentRepository = documentRepository;
        this.driverRepository = driverRepository;
        this.storageConfig = storageConfig;
    }

    public DocumentRepository getDocumentRepository() {
        return documentRepository;
    }

    public DriverRepository getDriverRepository() {
        return driverRepository;
    }

    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/jpg",
        "application/pdf"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public Document uploadDocument(Long driverId, DocumentType docType, MultipartFile file) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DocumentException("Driver not found", HttpStatus.NOT_FOUND));

        validateFile(file);

        documentRepository.findByDriverIdAndDocType(driverId, docType)
                .ifPresent(doc -> {
                    throw new DocumentException("Document already exists for this type", HttpStatus.BAD_REQUEST);
                });

        try {
            Path uploadPath = storageConfig.getUploadPath();
            Files.createDirectories(uploadPath);

            // Generate unique filename with original extension
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create document record
            Document document = new Document();
            document.setDriver(driver);
            document.setDocType(docType);
            document.setFilePath(filePath.toString());
            document.setVerificationStatus(VerificationStatus.PENDING);

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new DocumentException("Failed to store file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DocumentException("File is empty", HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new DocumentException(
                "File size exceeds maximum limit of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB",
                HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            throw new DocumentException(
                "File type not allowed. Allowed types: " + String.join(", ", ALLOWED_FILE_TYPES),
                HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Document getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Document not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Document> getDriverDocuments(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new DocumentException("Driver not found", HttpStatus.NOT_FOUND);
        }
        return documentRepository.findByDriverId(driverId);
    }

    @Override
    public void deleteDocument(Long documentId) {
        Document document = getDocument(documentId);
        try {
            Files.deleteIfExists(Path.of(document.getFilePath()));
            documentRepository.delete(document);
        } catch (IOException e) {
            throw new DocumentException("Failed to delete file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Document verifyDocument(Long documentId) {
        Document document = getDocument(documentId);
        document.setVerificationStatus(VerificationStatus.VERIFIED);
        return documentRepository.save(document);
    }

    @Override
    public boolean checkDocumentStatus(Long driverId, DocumentType docType) {
        return documentRepository.findByDriverIdAndDocType(driverId, docType)
                .map(doc -> doc.getVerificationStatus() == VerificationStatus.VERIFIED)
                .orElse(false);
    }
}