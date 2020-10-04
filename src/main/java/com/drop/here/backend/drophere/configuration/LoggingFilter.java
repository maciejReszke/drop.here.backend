package com.drop.here.backend.drophere.configuration;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Slf4j
@Component
@WebFilter
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Set<String> NOT_SUPPORTED_URLS = Set.of("swagger", "css", ".csrf", ".svg", ".js", ".png");

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        long start = System.currentTimeMillis();
        Try.run(() -> filterChain.doFilter(request, response))
                .andFinally(() -> logRequest(start, request, response));
    }

    private void logRequest(long start, HttpServletRequest request, HttpServletResponse response) {
        if (isRequestedUrlLoggingSupported(request.getRequestURI())) {
            long timeElapsed = System.currentTimeMillis() - start;
            log.info("Request: {}, Params: {}, Method: `{}`, Response status: `{}`, Lasted: {}ms", request.getRequestURI(), request.getQueryString(), request.getMethod(), response.getStatus(), timeElapsed);
        }
    }

    private boolean isRequestedUrlLoggingSupported(String requestURL) {
        return NOT_SUPPORTED_URLS.stream().noneMatch(requestURL::contains);
    }
}
