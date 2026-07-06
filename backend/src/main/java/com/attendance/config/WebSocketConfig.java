package com.attendance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration using STOMP protocol over SockJS.
 * Enables real-time live attendance updates on the admin dashboard.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.websocket.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.websocket.endpoint:/ws}")
    private String wsEndpoint;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(wsEndpoint)
                .setAllowedOriginPatterns(allowedOrigins.split(","))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for messages FROM client TO server
        registry.setApplicationDestinationPrefixes("/app");

        // Enable simple in-memory broker
        // Topics:
        // /topic/attendance       — new attendance event
        // /topic/session          — session status change
        // /topic/online-users     — connected users count update
        // /topic/notifications    — notifications
        registry.enableSimpleBroker(
            "/topic",
            "/queue"
        );

        // Prefix for messages TO specific user
        registry.setUserDestinationPrefix("/user");
    }
}
