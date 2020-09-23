package com.drop.here.backend.drophere.configuration.configuration;

import com.drop.here.backend.drophere.authentication.token.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private AuthenticationManager authenticationManager;


    @Test
    void givenValidTokenInAuthorizationWhenDoFilterInternalThenSetAuthentication() throws IOException, ServletException {
        //given
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer TOKEN");
        when(jwtService.decodeToken("TOKEN")).thenReturn(PreAuthentication.withoutProfile("to", LocalDateTime.now()));
        doNothing().when(filterChain).doFilter(httpServletRequest, httpServletResponse);

        //when
        new JwtAuthenticationFilter(authenticationManager, jwtService).doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //then
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void givenInvalidTokenInAuthorizationWhenDoFilterInternalThenDoFilter() throws IOException, ServletException {
        //given
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer TOKEN");
        when(jwtService.decodeToken("TOKEN")).thenThrow(new RuntimeException());
        doNothing().when(filterChain).doFilter(httpServletRequest, httpServletResponse);
        //when
        new JwtAuthenticationFilter(authenticationManager, jwtService).doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //then
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void givenNotBearerTokenInAuthorizationWhenDoFilterInternalThenDoFilter() throws IOException, ServletException {
        //given
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("bear");
        doNothing().when(filterChain).doFilter(httpServletRequest, httpServletResponse);

        //when
        new JwtAuthenticationFilter(authenticationManager, jwtService).doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //then
        verifyNoInteractions(jwtService);
    }

    @Test
    void givenLackOfAuthorizationWhenDoFilterInternalThenDoFilter() throws IOException, ServletException {
        //given
        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        doNothing().when(filterChain).doFilter(httpServletRequest, httpServletResponse);

        //when
        new JwtAuthenticationFilter(authenticationManager, jwtService).doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        //then
        verifyNoInteractions(jwtService);
    }


}