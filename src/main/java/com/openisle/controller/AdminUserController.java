package com.openisle.controller;

import com.openisle.model.User;
import com.openisle.service.EmailSender;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserRepository userRepository;
    private final EmailSender emailSender;
    @Value("${app.website-url}")
    private String websiteUrl;

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setApproved(true);
        userRepository.save(user);
        emailSender.sendEmail(user.getEmail(), "Registration Approved",
                "Your account has been approved. Visit: " + websiteUrl);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setApproved(false);
        userRepository.save(user);
        emailSender.sendEmail(user.getEmail(), "Registration Rejected",
                "Your account request was rejected. Visit: " + websiteUrl);
        return ResponseEntity.ok().build();
    }
}
