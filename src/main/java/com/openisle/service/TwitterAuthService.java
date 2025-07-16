package com.openisle.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.openisle.model.Role;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TwitterAuthService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${twitter.client-id:}")
    private String clientId;

    public Optional<User> authenticate(
            String code,
            String codeVerifier,
            RegisterMode mode,
            String redirectUri) {

        // 1. 交换 token
        String tokenUrl = "https://api.twitter.com/2/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // Twitter PKCE 要求的五个参数
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("code_verifier", codeVerifier);
        body.add("redirect_uri", redirectUri);      // 一律必填
        // 如果你的 app 属于机密客户端，必须带 client_secret
        // body.add("client_secret", clientSecret);

        ResponseEntity<JsonNode> tokenRes;
        try {
            tokenRes = restTemplate.postForEntity(tokenUrl, new HttpEntity<>(body, headers), JsonNode.class);
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }

        JsonNode tokenJson = tokenRes.getBody();
        if (tokenJson == null || !tokenJson.hasNonNull("access_token")) {
            return Optional.empty();
        }
        String accessToken = tokenJson.get("access_token").asText();

        // 2. 拉取用户信息
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);
        ResponseEntity<JsonNode> userRes;
        try {
            userRes = restTemplate.exchange(
                    "https://api.twitter.com/2/users/me",
                    HttpMethod.GET,
                    new HttpEntity<>(authHeaders),
                    JsonNode.class);
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }

        JsonNode data = userRes.getBody() == null ? null : userRes.getBody().path("data");
        String username = data != null ? data.path("username").asText(null) : null;
        if (username == null) {
            return Optional.empty();
        }

        // Twitter v2 默认拿不到 email；如果你申请到 email.scope，可改用 /2/users/:id?user.fields=email
        String email = username + "@twitter.com";
        return Optional.of(processUser(email, username, mode));
    }

    private User processUser(String email, String username, com.openisle.model.RegisterMode mode) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            if (!user.isVerified()) {
                user.setVerified(true);
                user.setVerificationCode(null);
                userRepository.save(user);
            }
            return user;
        }
        String baseUsername = username != null ? username : email.split("@")[0];
        String finalUsername = baseUsername;
        int suffix = 1;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = baseUsername + suffix++;
        }
        User user = new User();
        user.setUsername(finalUsername);
        user.setEmail(email);
        user.setPassword("");
        user.setRole(Role.USER);
        user.setVerified(true);
        user.setApproved(mode == com.openisle.model.RegisterMode.DIRECT);
        user.setAvatar("https://twitter.com/" + finalUsername + "/profile_image");
        return userRepository.save(user);
    }
}
