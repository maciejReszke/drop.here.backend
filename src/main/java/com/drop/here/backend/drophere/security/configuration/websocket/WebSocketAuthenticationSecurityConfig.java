package com.drop.here.backend.drophere.security.configuration.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketAuthChannelInterceptorAdapter webSocketAuthChannelInterceptorAdapter;

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
        registration.interceptors(webSocketAuthChannelInterceptorAdapter);
    }

}
