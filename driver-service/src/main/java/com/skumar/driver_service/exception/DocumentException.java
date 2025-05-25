package com.skumar.driver_service.exception;

import org.springframework.http.HttpStatus;

public class DocumentException extends RuntimeException {
    private HttpStatus status;

    public DocumentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public DocumentException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}