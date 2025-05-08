package com.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", ex.getMessage());
        errorResponse.put("errorCode", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalErrors(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
                "errorMessage", "An unexpected error occurred",
                "errorCode", "500"
        ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("errorMessage", ex.getMessage());
        errorBody.put("errorCode", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExistsException(AlreadyExistsException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", ex.getMessage());
        errorResponse.put("errorCode", HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ValidationNotPassedException.class)
    public ResponseEntity<Map<String, Object>> handleValidationNotPassedException(ValidationNotPassedException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", ex.getMessage());
        errorResponse.put("details", ex.getDetails());
        errorResponse.put("errorCode", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}