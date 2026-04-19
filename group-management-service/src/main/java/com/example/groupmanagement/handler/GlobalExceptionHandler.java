package com.example.groupmanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(
            ValidationException e, 
            WebRequest request) {
        
        logger.warn("❌ [GlobalExceptionHandler] Validation error: {}", e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation error");
        response.put("message", e.getMessage());
        if (e.getFieldName() != null) {
            response.put("field", e.getFieldName());
        }
        if (e.getErrorCode() != null) {
            response.put("code", e.getErrorCode());
        }
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {
        logger.warn("❌ [GlobalExceptionHandler] Request validation failed: {}", e.getMessage());

        List<Map<String, Object>> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.<String, Object>of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation error");
        response.put("message", "Invalid request data");
        response.put("details", errors);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            WebRequest request) {
        logger.warn("❌ [GlobalExceptionHandler] Malformed request body: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Malformed request");
        response.put("message", "Dữ liệu gửi lên không hợp lệ. Vui lòng kiểm tra lại groupName và adminId.");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(
            IllegalArgumentException e, 
            WebRequest request) {
        
        logger.warn("❌ [GlobalExceptionHandler] Illegal argument: {}", e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid argument");
        response.put("message", e.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(
            Exception e, 
            WebRequest request) {
        
        logger.error("❌ [GlobalExceptionHandler] Unexpected error: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", "Đã xảy ra lỗi không mong muốn");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}