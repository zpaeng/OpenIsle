package com.openisle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.openisle.exception.FieldException;
import com.openisle.exception.NotFoundException;
import com.openisle.exception.RateLimitException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FieldException.class)
    public ResponseEntity<?> handleFieldException(FieldException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", Objects.toString(ex.getMessage(), null));
        body.put("field", ex.getField());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", Objects.toString(ex.getMessage(), null));
        return ResponseEntity.status(404).body(body);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<?> handleRateLimitException(RateLimitException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", Objects.toString(ex.getMessage(), null));
        return ResponseEntity.status(429).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", Objects.toString(ex.getMessage(), null));
        return ResponseEntity.badRequest().body(body);
    }
}

