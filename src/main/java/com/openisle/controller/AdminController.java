package com.openisle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/*
✅
curl http://localhost:8080/api/admin/hello \
    -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X3VzZXIxIiwiaWF0IjoxNzUxMjg0OTU2LCJleHAiOjE3NTEzNzEzNTZ9.u84elcDTK2gIvuS4dKJCdE21pRSgY265fvdm9m9DnCQ"
 */

/**
 * Simple admin demo endpoint.
 */
@RestController
public class AdminController {
    @GetMapping("/api/admin/hello")
    public Map<String, String> adminHello() {
        return Map.of("message", "Hello, Admin User");
    }
}
