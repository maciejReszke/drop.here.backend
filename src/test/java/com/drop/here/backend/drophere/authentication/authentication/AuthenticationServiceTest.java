package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.account.service.BaseLoginRequest;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthenticationExecutiveService authenticationExecutiveService;

    @Test
    void givenValidBaseRequestWhenLoginThenLogin() {
        //given
        final BaseLoginRequest baseLoginRequest = BaseLoginRequest.builder()
                .mail("mail")
                .password("password")
                .build();

        final Account account = AccountDataGenerator.companyAccount(1);
        final LoginResponse response = new LoginResponse("token", "validUntil");

        when(accountService.findActiveAccountByMail(baseLoginRequest.getMail())).thenReturn(Optional.of(account));
        when(accountService.isPasswordValid(account, baseLoginRequest.getPassword())).thenReturn(true);
        when(authenticationExecutiveService.successLogin(account)).thenReturn(response);

        //when
        final LoginResponse result = authenticationService.login(baseLoginRequest);

        //then
        assertThat(result).isEqualTo(response);
    }

    @Test
    void givenNotExistingAccountWhenLoginThenError() {
        //given
        final BaseLoginRequest baseLoginRequest = BaseLoginRequest.builder()
                .mail("mail")
                .password("password")
                .build();

        when(accountService.findActiveAccountByMail(baseLoginRequest.getMail())).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> authenticationService.login(baseLoginRequest));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((UnauthorizedRestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.LOGIN_ACTIVE_USER_NOT_FOUND.ordinal());
    }

    @Test
    void givenInvalidPasswordWhenLoginThenError() {
        //given
        final BaseLoginRequest baseLoginRequest = BaseLoginRequest.builder()
                .mail("mail")
                .password("password")
                .build();
        final Account account = AccountDataGenerator.companyAccount(1);

        when(accountService.findActiveAccountByMail(baseLoginRequest.getMail())).thenReturn(Optional.of(account));
        when(accountService.isPasswordValid(account, baseLoginRequest.getPassword())).thenReturn(false);

        //when
        final Throwable throwable = catchThrowable(() -> authenticationService.login(baseLoginRequest));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((UnauthorizedRestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.LOGIN_INVALID_PASSWORD.ordinal());
    }

    @Test
    void givenAccountAuthenticationWhenGetAuthenticationInfoThenGet() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final AuthenticationResponse info = AuthenticationResponse.builder().build();
        when(authenticationExecutiveService.getAuthenticationInfo(accountAuthentication)).thenReturn(info);

        //when
        final AuthenticationResponse authenticationInfo = authenticationService.getAuthenticationInfo(accountAuthentication);

        //then
        assertThat(authenticationInfo).isEqualTo(info);
    }

}