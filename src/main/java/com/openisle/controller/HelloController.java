package com.openisle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/*
curl http://localhost:8080/api/hello \
    -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X3VzZXIiLCJpYXQiOjE3NTEyODAzMjksImV4cCI6MTc1MTM2NjcyOX0.XNDGTQd1H9u3ZOYtnJaU5fL5zhtwyZZm5aX3vL_my1c"
 */

@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello, Authenticated User");
    }
}
