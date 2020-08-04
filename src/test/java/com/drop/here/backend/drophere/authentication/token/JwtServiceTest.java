package com.drop.here.backend.drophere.authentication.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.security.configuration.PreAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang.reflect.FieldUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(jwtService, "validInMinutes", 60, true);
        FieldUtils.writeDeclaredField(jwtService, "secret", "abc", true);
        FieldUtils.writeDeclaredField(jwtService, "issuer", "issuer", true);
        FieldUtils.writeDeclaredField(jwtService, "algorithm", Algorithm.HMAC512("abc".getBytes()), true);
    }

    @Test
    void givenAccountWhenCreateTokenThenCreate() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);

        //when
        final TokenResponse response = jwtService.createToken(account);

        //then
        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getValidUntil()).isBetween(LocalDateTime.now().plusMinutes(59), LocalDateTime.now().plusMinutes(60));
        assertThat(JWT.decode(response.getToken()).getToken()).isEqualTo(response.getToken());
    }

    @Test
    void givenValidJwtWhenDecodeTokenThenRespond() {
        //given
        final LocalDateTime date = LocalDateTime.now().plusMinutes(15);
        final String token = JWT.create()
                .withIssuer("issuer")
                .withAudience("issuer")
                .withSubject("subject")
                .withExpiresAt(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512("abc".getBytes()));

        //when
        final PreAuthentication result = jwtService.decodeToken(token);

        //then
        assertThat(result.getMail()).isEqualTo("subject");
        assertThat(result.getValidUntil()).isBetween(date.minusSeconds(1), date.plusSeconds(1));
    }

    @Test
    void givenExpiredJwtWhenDecodeTokenThenError() {
        //given
        final String token = JWT.create()
                .withIssuer("issuer")
                .withAudience("issuer")
                .withSubject("subject")
                .withExpiresAt(Date.from(LocalDateTime.now().minusMinutes(15).atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512("abc".getBytes()));

        //when
        final Throwable throwable = catchThrowable(() -> jwtService.decodeToken(token));

        //then
        assertThat(throwable).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenWithInvalidIssuerJwtWhenDecodeTokenThenError() {
        //given
        final String token = JWT.create()
                .withIssuer("issuer2")
                .withAudience("issuer")
                .withSubject("subject")
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512("abc".getBytes()));

        //when
        final Throwable throwable = catchThrowable(() -> jwtService.decodeToken(token));

        //then
        assertThat(throwable).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenWithInvalidAudienceWhenDecodeTokenThenError() {
        //given
        final String token = JWT.create()
                .withIssuer("issuer")
                .withAudience("issuer2")
                .withSubject("subject")
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512("abc".getBytes()));

        //when
        final Throwable throwable = catchThrowable(() -> jwtService.decodeToken(token));

        //then
        assertThat(throwable).isInstanceOf(RuntimeException.class);
    }

    @Test
    void givenInvalidEncodingWhenDecodeTokenThenError() {
        //given
        final String token = JWT.create()
                .withIssuer("issuer")
                .withAudience("issuer")
                .withSubject("subject")
                .withExpiresAt(Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512("def".getBytes()));

        //when
        final Throwable throwable = catchThrowable(() -> jwtService.decodeToken(token));

        //then
        assertThat(throwable).isInstanceOf(RuntimeException.class);
    }

}