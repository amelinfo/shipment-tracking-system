package com.logistics.tracking_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TrackingResult> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        TrackingResult errorResult = new TrackingResult(
            null, null, false, errorMessage, false, LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorResult);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<TrackingResult> handleGenericException(Exception ex) {
        TrackingResult errorResult = new TrackingResult(
            null, null, false, "Internal server error", false, LocalDateTime.now()
        );
        return ResponseEntity.internalServerError().body(errorResult);
    }
}