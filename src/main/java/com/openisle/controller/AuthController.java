package com.openisle.controller;

import com.openisle.model.User;
import com.openisle.service.EmailSender;
import com.openisle.service.JwtService;
import com.openisle.service.UserService;
import com.openisle.service.CaptchaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailSender emailService;
    private final CaptchaService captchaService;

    @Value("${app.captcha.enabled:false}")
    private boolean captchaEnabled;

    @Value("${app.captcha.register-enabled:false}")
    private boolean registerCaptchaEnabled;

    @Value("${app.captcha.login-enabled:false}")
    private boolean loginCaptchaEnabled;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (captchaEnabled && registerCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid captcha"));
        }
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
        if (captchaEnabled && loginCaptchaEnabled && !captchaService.verify(req.getCaptcha())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid captcha"));
        }
        Optional<User> user = userService.authenticate(req.getUsername(), req.getPassword());
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of("token", jwtService.generateToken(user.get().getUsername())));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials or user not verified"));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkToken() {
        return ResponseEntity.ok(Map.of("valid", true));
    }

    @Data
    private static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String captcha;
    }

    @Data
    private static class LoginRequest {
        private String username;
        private String password;
        private String captcha;
    }

    @Data
    private static class VerifyRequest {
        private String username;
        private String code;
    }
}
