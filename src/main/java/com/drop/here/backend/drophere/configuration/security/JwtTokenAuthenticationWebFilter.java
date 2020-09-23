package com.drop.here.backend.drophere.configuration.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

public class JwtTokenAuthenticationWebFilter extends AuthenticationWebFilter {
    public JwtTokenAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager,
                                           JwtTokenAuthenticationExtractor jwtTokenAuthenticationExtractor,
                                           ServerWebExchangeMatcher requiresAuthenticationMatcher,
                                           ForbiddenServerEntryPoint forbiddenServerEntryPoint) {
        super(authenticationManager);
        setServerAuthenticationConverter(jwtTokenAuthenticationExtractor);
        setRequiresAuthenticationMatcher(requiresAuthenticationMatcher);
        setAuthenticationFailureHandler((webFilterExchange, exception) -> forbiddenServerEntryPoint.commence(webFilterExchange.getExchange(), exception));
    }
}
