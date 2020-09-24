package com.drop.here.backend.drophere.configuration.security;

import com.drop.here.backend.drophere.authentication.token.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationExtractor implements ServerAuthenticationConverter {
    private final JwtService jwtService;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(extract(exchange))
                .flatMap(this::buildAuthentication);
    }

    private Mono<Authentication> buildAuthentication(String token) {
        try {
            return Mono.just(jwtService.decodeToken(token));
        } catch (Exception exception) {
            log.warn("Decoding token ended with failure and message is: {}", exception.getMessage());
            return Mono.empty();
        }
    }

    private String extract(ServerWebExchange serverWebExchange) {
        return serverWebExchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
    }
}
