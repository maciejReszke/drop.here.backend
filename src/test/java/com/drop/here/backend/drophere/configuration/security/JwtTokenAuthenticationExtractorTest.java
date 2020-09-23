package com.drop.here.backend.drophere.configuration.security;

import com.drop.here.backend.drophere.authentication.token.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenAuthenticationExtractorTest {

    @InjectMocks
    private JwtTokenAuthenticationExtractor jwtTokenAuthenticationExtractor;

    @Mock
    private JwtService jwtService;

    @Mock
    private ServerWebExchange serverWebExchange;

    @Mock
    private ServerHttpRequest serverHttpRequest;

    @Test
    void givenCorrectTokenWhenConvertThenConvert() {
        //given
        final PreAuthentication preAuthentication = PreAuthentication.withProfile("a", "b", LocalDateTime.now());

        when(jwtService.decodeToken("token")).thenReturn(preAuthentication);
        when(serverWebExchange.getRequest()).thenReturn(serverHttpRequest);
        final LinkedMultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        headersMap.put("AUTHORIZATION", List.of("token"));
        when(serverHttpRequest.getHeaders()).thenReturn(new HttpHeaders(headersMap));

        //when
        final Mono<Authentication> result = jwtTokenAuthenticationExtractor.convert(serverWebExchange);

        //then
        StepVerifier.create(result)
                .expectNext(preAuthentication)
                .verifyComplete();
    }

    @Test
    void givenInvalidTokenWhenConvertThenEmpty() {
        //given
        final PreAuthentication preAuthentication = PreAuthentication.withProfile("a", "b", LocalDateTime.now());

        when(serverWebExchange.getRequest()).thenReturn(serverHttpRequest);
        final LinkedMultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        headersMap.put("AUTHORIZATION", null);
        when(serverHttpRequest.getHeaders()).thenReturn(new HttpHeaders(headersMap));

        //when
        final Mono<Authentication> result = jwtTokenAuthenticationExtractor.convert(serverWebExchange);

        //then
        StepVerifier.create(result)
                .expectNext(preAuthentication)
                .verifyComplete();
    }
}