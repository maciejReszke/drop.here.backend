package com.drop.here.backend.drophere.configuration.logging;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {
    private static final String PAYLOAD_ERROR = "ERROR";
    private static final String PAYLOAD_EMPTY = "";

    private final boolean logBody;
    private final String requestId;
    private final long requestStartTime;
    private final Set<String> prohibitedLoggingHeaders;

    private boolean loggedResponseBody = false;
    private boolean loggedResponseBase = false;

    public ResponseLoggingInterceptor(ServerHttpResponse delegate, boolean logBody, String requestId, long requestStartTime, Set<String> prohibitedLoggingHeaders) {
        super(delegate);
        this.logBody = logBody;
        this.requestId = requestId;
        this.requestStartTime = requestStartTime;
        this.prohibitedLoggingHeaders = prohibitedLoggingHeaders;
    }

    @Override
    public Mono<Void> setComplete() {
        return logBaseResponse().then(super.setComplete());
    }

    @Override
    public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
        return super.writeWith(Flux.from(body)
                .doOnNext(this::logResponse));
    }

    private Mono<Void> logBaseResponse() {
        if (!loggedResponseBase) {
            log.info("Response({} ms): id={}, status={}, headers {}", System.currentTimeMillis() - requestStartTime, requestId, getDelegate().getRawStatusCode(), filteredHeaders());
            loggedResponseBase = true;
        }
        return Mono.empty();
    }

    private void logResponse(DataBuffer dataBuffer) {
        if (!loggedResponseBase) {
            logBaseResponse().block();
        }
        if (!loggedResponseBody && logBody) {
            loggedResponseBody = true;
            final String body = getBody(dataBuffer).orElse(PAYLOAD_EMPTY);
            log.info("Response: id={}, payload={}", requestId, body);
        }
    }

    private HttpHeaders filteredHeaders() {
        return new HttpHeaders(new LinkedMultiValueMap<>(getDelegate().getHeaders()
                .entrySet()
                .stream()
                .filter(headerPair -> !prohibitedLoggingHeaders.contains(headerPair.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    private Optional<String> getBody(DataBuffer dataBuffer) {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream();
             final WritableByteChannel writableByteChannel = Channels.newChannel(stream)) {
            writableByteChannel.write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
            return Optional.of(new String(stream.toByteArray(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Exception during extracting response body with id {}", requestId, e);
            return Optional.of(PAYLOAD_ERROR);
        }
    }
}
