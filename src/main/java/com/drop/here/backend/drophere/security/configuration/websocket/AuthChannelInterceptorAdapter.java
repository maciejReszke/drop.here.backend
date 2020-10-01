package com.drop.here.backend.drophere.security.configuration.websocket;

import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.security.configuration.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {
    private final JwtService jwtService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT == accessor.getCommand()) {
            final Authentication authentication = Optional.ofNullable(accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION))
                    .filter(token -> token.startsWith(BEARER_PREFIX))
                    .map(token -> token.replace(BEARER_PREFIX, ""))
                    .map(jwtService::decodeToken)
                    .map(jwtAuthenticationProvider::authenticate)
                    .orElse(null);

            accessor.setUser(authentication);
        }
        return message;
    }
}
