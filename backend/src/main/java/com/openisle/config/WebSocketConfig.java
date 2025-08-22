package com.openisle.config;

import com.openisle.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Value("${app.website-url}")
    private String websiteUrl;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry the messages back to the client on destinations prefixed with "/topic" and "/queue"
        config.enableSimpleBroker("/topic", "/queue");
        // Set user destination prefix for personal messages
        config.setUserDestinationPrefix("/user");
        // Designates the "/app" prefix for messages that are bound for @MessageMapping-annotated methods.
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ① 原生 WebSocket 端点：用 patterns，抗 www/端口漂移
        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns(
                        // 本地
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://192.168.7.98:*",
                        "http://30.211.97.238:*",
                        // 线上
                        "https://staging.open-isle.com",
                        "https://www.staging.open-isle.com",
                        websiteUrl,
                        websiteUrl.replace("://www.", "://")
                );

        // ② SockJS 注册：要单独再配一次，且只能 exact，不支持 patterns
        registry.addEndpoint("/api/ws")
                .setAllowedOrigins(
                        // 本地（端口要写死）
                        "http://localhost:3000",
                        "http://localhost:3001",
                        "http://127.0.0.1:3000",
                        "http://127.0.0.1:3001",
                        "http://192.168.7.98",
                        "http://192.168.7.98:3000",
                        "http://30.211.97.238",
                        "http://30.211.97.238:3000",
                        // 线上
                        "https://staging.open-isle.com",
                        "https://www.staging.open-isle.com",
                        websiteUrl,
                        websiteUrl.replace("://www.", "://")
                )                .withSockJS()
                .setSessionCookieNeeded(false) // 避免强依赖 JSESSIONID
                .setWebSocketEnabled(true);
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    System.out.println("WebSocket CONNECT command received");
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("Authorization header: " + (authHeader != null ? "present" : "missing"));
                    
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        try {
                            String username = jwtService.validateAndGetSubject(token);
                            System.out.println("JWT validated for user: " + username);
                            var userDetails = userDetailsService.loadUserByUsername(username);
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                            accessor.setUser(auth);
                            System.out.println("WebSocket user set: " + username);
                        } catch (Exception e) {
                            System.err.println("JWT validation failed: " + e.getMessage());
                        }
                    }
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    System.out.println("WebSocket SUBSCRIBE to: " + accessor.getDestination());
                    System.out.println("WebSocket user during subscribe: " + (accessor.getUser() != null ? accessor.getUser().getName() : "null"));
                }
                return message;
            }
        });
    }
}