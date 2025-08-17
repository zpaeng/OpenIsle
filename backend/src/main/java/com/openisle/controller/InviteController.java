package com.openisle.controller;

import com.openisle.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/invite")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    @PostMapping("/generate")
    public Map<String, String> generate(Authentication auth) {
        String token = inviteService.generate(auth.getName());
        return Map.of("token", token);
    }
}
