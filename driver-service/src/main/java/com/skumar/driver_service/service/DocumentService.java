package com.skumar.driver_service.service;

import com.skumar.driver_service.entity.Document;
import com.skumar.driver_service.entity.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    Document uploadDocument(Long driverId, DocumentType docType, MultipartFile file);
    
    Document getDocument(Long documentId);
    
    List<Document> getDriverDocuments(Long driverId);
    
    void deleteDocument(Long documentId);
    
    Document verifyDocument(Long documentId);
    
    boolean checkDocumentStatus(Long driverId, DocumentType docType);
}