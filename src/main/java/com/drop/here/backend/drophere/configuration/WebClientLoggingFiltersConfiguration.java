package com.drop.here.backend.drophere.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

// TODO: 23/09/2020 use
@Configuration
@Slf4j
public class WebClientLoggingFiltersConfiguration {
    private static final String HEADERS_VALUE_DELIMITER = ",";
    private static final String HEADER_NAME_VALUE_DELIMITER = ":";
    private static final String HEADERS_DELIMITER = ";";

    @Bean(name = "logRequestFilter")
    public ExchangeFilterFunction logRequestFilter(@Value("${web_client.logging.prohibited_logging_headers}") Set<String> prohibitedLoggingHeaders) {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request [{}]: method={}, url={}, headers={}", clientRequest.logPrefix(), clientRequest.method(), clientRequest.url(), getHeaders(prohibitedLoggingHeaders, clientRequest.headers()));
            return Mono.just(clientRequest);
        });
    }

    @Bean(name = "logResponseFilter")
    public ExchangeFilterFunction logResponseFilter(@Value("${web_client.logging.prohibited_logging_headers}") Set<String> prohibitedLoggingHeaders) {
        return ExchangeFilterFunction.ofResponseProcessor(clientRequest -> {
            log.info("Response[{}]: code={}, headers={}", clientRequest.logPrefix(), clientRequest.statusCode(), getHeaders(prohibitedLoggingHeaders, clientRequest.headers().asHttpHeaders()));
            return Mono.just(clientRequest);
        });
    }

    private String getHeaders(Set<String> prohibitedLoggingHeaders, HttpHeaders headers) {
        return headers.entrySet()
                .stream()
                .filter(headerName -> !prohibitedLoggingHeaders.contains(headerName.toString().toUpperCase()))
                .map(header -> header.getKey() + HEADER_NAME_VALUE_DELIMITER + String.join(HEADERS_VALUE_DELIMITER, header.getValue()))
                .collect(Collectors.joining(HEADERS_DELIMITER));
    }
}
