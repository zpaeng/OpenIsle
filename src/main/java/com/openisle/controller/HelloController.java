package com.openisle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    /**
     * curl http://localhost:8080/api/hello \
     *   -H "Authorization: Bearer <jwt-token>"
     */
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, Authenticated User";
    }
}
