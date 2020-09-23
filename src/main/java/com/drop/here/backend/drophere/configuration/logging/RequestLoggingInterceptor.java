package com.drop.here.backend.drophere.configuration.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class RequestLoggingInterceptor extends ServerHttpRequestDecorator {
    private static final String PAYLOAD_ERROR = "ERROR";
    private static final String PAYLOAD_EMPTY = "";
    private static final String IP_EMPTY = "";

    private final String requestUid;
    private final boolean logBody;
    private final Set<String> prohibitedLoggingHeaders;

    public RequestLoggingInterceptor(ServerHttpRequest delegate, boolean logBody, String requestUid, Set<String> prohibitedLoggingHeaders) {
        super(delegate);
        this.logBody = logBody;
        this.prohibitedLoggingHeaders = prohibitedLoggingHeaders;
        this.requestUid = requestUid;
        logBaseRequest();
    }

    private void logBaseRequest() {
        log.info("Request: id={}, ip={}, method={}, uri={}, headers={}", requestUid, getIp(), getDelegate().getMethod(), getDelegate().getPath(), filteredHeaders());
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(this::logRequestBody);
    }

    private void logRequestBody(DataBuffer dataBuffer) {
        if (logBody) {
            log.info("Request: id={}, payload={}", requestUid, getBody(dataBuffer).orElse(PAYLOAD_EMPTY));
        }
    }

    private HttpHeaders filteredHeaders() {
        return new HttpHeaders(new LinkedMultiValueMap<>(getDelegate().getHeaders()
                .entrySet()
                .stream()
                .filter(headerPair -> !prohibitedLoggingHeaders.contains(headerPair.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    private String getIp() {
        return Optional.ofNullable(getDelegate().getRemoteAddress())
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .orElse(IP_EMPTY);
    }

    private Optional<String> getBody(DataBuffer dataBuffer) {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream();
             final WritableByteChannel writableByteChannel = Channels.newChannel(stream)) {
            writableByteChannel.write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
            return Optional.of(new String(stream.toByteArray(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Exception during extracting request body with id {}", requestUid, e);
            return Optional.of(PAYLOAD_ERROR);
        }
    }
}
