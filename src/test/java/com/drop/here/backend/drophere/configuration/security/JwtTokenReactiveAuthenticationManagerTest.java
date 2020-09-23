package com.drop.here.backend.drophere.configuration.security;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.authentication.exception.UnauthorizedRestException;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenReactiveAuthenticationManagerTest {

    @InjectMocks
    private JwtTokenReactiveAuthenticationManager jwtAuthenticationProvider;

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
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Mono.just(account));
        when(authenticationBuilder.buildAuthentication(account, authentication)).thenReturn(accountAuthentication);

        //when
        final Mono<Authentication> result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        StepVerifier.create(result)
                .expectNext(accountAuthentication)
                .verifyComplete();
    }

    @Test
    void givenNotExistingAccountAuthenticationWithoutProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withoutProfile("mail", LocalDateTime.now());

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Mono.empty());

        //when
        final Mono<Authentication> result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        StepVerifier.create(result)
                .expectError(UnauthorizedRestException.class)
                .verify();
    }

    @Test
    void givenNotExistingAccountAuthenticationWithProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withProfile("mail", "profileUid", LocalDateTime.now());

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Mono.empty());

        //when
        final Mono<Authentication> result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        StepVerifier.create(result)
                .expectError(UnauthorizedRestException.class)
                .verify();
    }


    @Test
    void givenNotExistingProfileAccountAuthenticationWithProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withProfile("mail", "profileUid", LocalDateTime.now());
        final Account account = AccountDataGenerator.companyAccount(1);

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Mono.just(account));
        when(accountProfileService.findActiveProfile(account, "profileUid")).thenReturn(Mono.empty());

        //when
        final Mono<Authentication> result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        StepVerifier.create(result)
                .expectError(UnauthorizedRestException.class)
                .verify();
    }

    @Test
    void givenExistingProfileAccountAuthenticationWithProfileWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = PreAuthentication.withProfile("mail", "profileUid", LocalDateTime.now());
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Mono.just(account));
        when(accountProfileService.findActiveProfile(account, "profileUid")).thenReturn(Mono.just(accountProfile));
        when(authenticationBuilder.buildAuthentication(account, accountProfile, authentication)).thenReturn(accountAuthentication);

        //when
        final Mono<Authentication> result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        StepVerifier.create(result)
                .expectNext(accountAuthentication)
                .verifyComplete();
    }

}