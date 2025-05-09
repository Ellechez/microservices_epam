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
        errorResponse.put("errorCode", String.valueOf(HttpStatus.NOT_FOUND.value()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalErrors(Exception ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("errorMessage", "An unexpected error occurred");
        errorBody.put("errorCode", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("errorMessage", ex.getMessage());
        errorBody.put("errorCode", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExistsException(AlreadyExistsException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", ex.getMessage());
        errorResponse.put("errorCode", String.valueOf(HttpStatus.CONFLICT.value()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ValidationNotPassedException.class)
    public ResponseEntity<Map<String, Object>> handleValidationNotPassedException(ValidationNotPassedException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", ex.getMessage());
        errorResponse.put("details", ex.getDetails());
        errorResponse.put("errorCode", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}