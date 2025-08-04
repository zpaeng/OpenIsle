package com.openisle.controller;

import com.openisle.dto.PushPublicKeyDto;
import com.openisle.dto.PushSubscriptionRequest;
import com.openisle.service.PushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushSubscriptionController {
    private final PushSubscriptionService pushSubscriptionService;
    @Value("${app.webpush.public-key}")
    private String publicKey;

    @GetMapping("/public-key")
    public PushPublicKeyDto getPublicKey() {
        PushPublicKeyDto r = new PushPublicKeyDto();
        r.setKey(publicKey);
        return r;
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody PushSubscriptionRequest req, Authentication auth) {
        pushSubscriptionService.saveSubscription(auth.getName(), req.getEndpoint(), req.getP256dh(), req.getAuth());
    }
}
