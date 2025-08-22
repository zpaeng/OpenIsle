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
        registry.addEndpoint("/api/ws")
                // 安全改进：使用具体的允许源，而不是通配符
                .setAllowedOrigins(
                        // 本地开发
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://192.168.7.98:*",
                        "http://30.211.97.238:*",
                        websiteUrl,
                        websiteUrl.replace("://www.", "://")

                        // 线上域名（务必是 https）
                        "https://staging.open-isle.com",
                        "https://www.staging.open-isle.com"
                )
                .withSockJS();
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