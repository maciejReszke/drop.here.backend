package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.token.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final JwtService jwtService;

    public static final String BEARER_PREFIX = "Bearer ";

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final Optional<String> maybeToken = getTokenFromAuthorization(request);

        if (maybeToken.isPresent()) {
            final String token = maybeToken.get();
            try {
                final PreAuthentication preAuthentication = jwtService.decodeToken(token);
                final Authentication authentication = getAuthenticationManager().authenticate(preAuthentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.info("Authentication by authorization header jwt token failed - invalid token {}", token);
            }
        }

        chain.doFilter(request, response);
    }

    private Optional<String> getTokenFromAuthorization(HttpServletRequest request) {
        final String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return tokenHeader != null && tokenHeader.startsWith(BEARER_PREFIX)
                ? Optional.of(tokenHeader.replace(BEARER_PREFIX, StringUtils.EMPTY))
                : Optional.empty();
    }
}
