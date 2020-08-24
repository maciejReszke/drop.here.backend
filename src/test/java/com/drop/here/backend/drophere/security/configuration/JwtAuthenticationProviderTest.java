package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.authentication.UnauthorizedRestException;
import com.drop.here.backend.drophere.common.exceptions.RestException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {
    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthenticationBuilder authenticationBuilder;

    @Mock
    private AccountProfileService accountProfileService;

    @Test
    void givenExistingAccountAuthenticationWithoutProfileWhenAuthenticateThenAuthenticate() {
        //given
        final PreAuthentication authentication = PreAuthentication.withoutProfile("mail", LocalDateTime.now());
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Optional.of(account));
        when(authenticationBuilder.buildAuthentication(account, authentication)).thenReturn(accountAuthentication);

        //when
        final Authentication result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        assertThat(result).isEqualTo(accountAuthentication);
    }

    @Test
    void givenNotExistingAccountAuthenticationWithoutProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withoutProfile("mail", LocalDateTime.now());

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> jwtAuthenticationProvider.authenticate(authentication));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((RestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_ACCOUNT.ordinal());
    }

    @Test
    void givenNotExistingAccountAuthenticationWithProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withProfile("mail", "profileUid", LocalDateTime.now());

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> jwtAuthenticationProvider.authenticate(authentication));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((RestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_ACCOUNT.ordinal());
    }


    @Test
    void givenNotExistingProfileAccountAuthenticationWithProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withProfile("mail", "profileUid", LocalDateTime.now());
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Optional.of(account));
        when(accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, "profileUid")).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> jwtAuthenticationProvider.authenticate(authentication));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((RestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_PROFILE.ordinal());
    }

    @Test
    void givenExistingProfileAccountAuthenticationWithProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withProfile("mail", "profileUid", LocalDateTime.now());
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Optional.of(account));
        when(accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, "profileUid")).thenReturn(Optional.of(accountProfile));
        when(authenticationBuilder.buildAuthentication(account, accountProfile, authentication)).thenReturn(accountAuthentication);

        //when
        final Authentication result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        assertThat(result).isEqualTo(accountAuthentication);
    }


}