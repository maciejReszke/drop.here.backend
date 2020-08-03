package com.drop.here.backend.drophere.authentication.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang.reflect.FieldUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(jwtService, "validInMinutes", 60, true);
        FieldUtils.writeDeclaredField(jwtService, "secret", "abc", true);
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

}