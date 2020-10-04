package com.drop.here.backend.drophere.security.configuration.websocket;

import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.security.configuration.JwtAuthenticationProvider;
import com.drop.here.backend.drophere.security.configuration.websocket.authorization.WebSocketSubscribingAuthorizationService;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthChannelInterceptorAdapter implements ChannelInterceptor {
    private final JwtService jwtService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final WebSocketSubscribingAuthorizationService authorizationService;

    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            throw new AccessDeniedException("Lack of accessor during socket connection");
        }

        API.Match(accessor.getCommand()).of(
                Case($(StompCommand.CONNECT), () -> API.run(() -> authenticate(accessor))),
                Case($(StompCommand.SUBSCRIBE), () -> API.run(() -> authorizeSubscription(accessor))),
                Case($(), () -> API.run(() -> {
                }))
        );

        return message;
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        authorizationService.authorize((AccountAuthentication) accessor.getUser(), accessor.getDestination());
    }

    private void authenticate(StompHeaderAccessor accessor) {
        final Authentication authentication = Optional.ofNullable(accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.startsWith(BEARER_PREFIX))
                .map(token -> token.replace(BEARER_PREFIX, ""))
                .map(jwtService::decodeToken)
                .map(jwtAuthenticationProvider::authenticate)
                .orElse(null);

        accessor.setUser(authentication);
    }
}
