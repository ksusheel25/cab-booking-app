package com.skumar.driver_service.repository;

import com.skumar.driver_service.entity.Document;
import com.skumar.driver_service.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByDriverId(Long driverId);
    
    Optional<Document> findByDriverIdAndDocType(Long driverId, DocumentType docType);
    
    boolean existsByDriverIdAndDocType(Long driverId, DocumentType docType);
}