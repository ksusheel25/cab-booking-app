package com.skumar.driver_service.service;

import com.skumar.driver_service.entity.AuditLog;
import com.skumar.driver_service.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditLogService {
    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(String username, String action, String endpoint, String method, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setEndpoint(endpoint);
        log.setMethod(method);
        log.setDetails(details);
        log.setTimestamp(Instant.now());
        repository.save(log);
    }
}
