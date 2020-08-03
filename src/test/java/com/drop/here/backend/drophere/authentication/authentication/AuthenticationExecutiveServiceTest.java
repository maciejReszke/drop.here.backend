package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationExecutiveServiceTest {

    @InjectMocks
    private AuthenticationExecutiveService authenticationExecutiveService;

    @Mock
    private JwtService jwtService;

    @Test
    void givenAccountWhenSuccessLoginThenLogin() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);

        when(jwtService.createToken(account)).thenReturn(new TokenResponse("token", LocalDateTime.of(2000, 1, 1, 1, 1, 1)));

        //when
        final LoginResponse response = authenticationExecutiveService.successLogin(account);

        //then
        assertThat(response.getToken()).isEqualTo("token");
        assertThat(response.getAccountType()).isEqualTo(account.getAccountType());
        assertThat(response.getTokenValidUntil()).isEqualTo("2000-01-01T01:01:01");
    }

}