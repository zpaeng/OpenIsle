package com.openisle.controller;

import com.openisle.model.User;
import com.openisle.service.EmailSender;
import com.openisle.service.JwtService;
import com.openisle.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailSender emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req.getUsername(), req.getEmail(), req.getPassword());
        emailService.sendEmail(user.getEmail(), "Verification Code", "Your verification code is " + user.getVerificationCode());
        return ResponseEntity.ok(Map.of("message", "Verification code sent"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest req) {
        boolean ok = userService.verifyCode(req.getUsername(), req.getCode());
        if (ok) {
            return ResponseEntity.ok(Map.of("message", "Verified"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification code"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> user = userService.authenticate(req.getUsername(), req.getPassword());
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of("token", jwtService.generateToken(user.get().getUsername())));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials or user not verified"));
        }
    }

    @Data
    private static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data
    private static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    private static class VerifyRequest {
        private String username;
        private String code;
    }
}
