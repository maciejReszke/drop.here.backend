package com.drop.here.backend.drophere.security.configuration.websocket;

import com.drop.here.backend.drophere.location.endpoint.LocationWebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String WEB_SOCKET_DESTINATION_PREFIX = "/stream";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(LocationWebSocketController.ENDPOINT);
        config.setApplicationDestinationPrefixes(WEB_SOCKET_DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(LocationWebSocketController.ENDPOINT).setAllowedOrigins("*");
        registry.addEndpoint(LocationWebSocketController.ENDPOINT).setAllowedOrigins("*").withSockJS();
    }
}
