package com.drop.here.backend.drophere.configuration.logging;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public class ReactiveSpringLoggingFilter implements WebFilter {
    private final boolean logRequestBody;
    private final boolean logResponseBody;
    private final Set<String> prohibitedLoggingHeaders;

    public ReactiveSpringLoggingFilter(boolean logRequestBody, boolean logResponseBody) {
        this.logRequestBody = logRequestBody;
        this.logResponseBody = logResponseBody;
        this.prohibitedLoggingHeaders = Set.of(HttpHeaders.AUTHORIZATION);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final ServerWebExchangeDecorator exchangeDecorator = new ServerWebExchangeDecorator(exchange) {
            private final String requestId = UUID.randomUUID().toString();
            private final RequestLoggingInterceptor requestLoggingInterceptor = new RequestLoggingInterceptor(super.getRequest(), logRequestBody, requestId, prohibitedLoggingHeaders);
            private final ResponseLoggingInterceptor responseLoggingInterceptor = new ResponseLoggingInterceptor(super.getResponse(), logResponseBody, requestId, System.currentTimeMillis(), prohibitedLoggingHeaders);

            @Override
            public ServerHttpRequest getRequest() {
                return requestLoggingInterceptor;
            }

            @Override
            public ServerHttpResponse getResponse() {
                return responseLoggingInterceptor;
            }
        };
        return chain.filter(exchangeDecorator);
    }
}