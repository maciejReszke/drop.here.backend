package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.authentication.UnauthorizedRestException;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
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

    @Test
    void givenExistingAccountAuthenticationWhenAuthenticateThenAuthenticate() {
        //given
        final PreAuthentication authentication = new PreAuthentication("mail", LocalDateTime.now());
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Optional.of(account));
        when(authenticationBuilder.buildAuthentication(account, authentication)).thenReturn(accountAuthentication);

        //when
        final Authentication result = jwtAuthenticationProvider.authenticate(authentication);

        //then
        assertThat(result).isEqualTo(accountAuthentication);
    }

    @Test
    void givenNotExistingAccountAuthenticationWhenAuthenticateThenThrowException() {
        //given
        final PreAuthentication authentication = new PreAuthentication("mail", LocalDateTime.now());

        when(accountService.findActiveAccountByMailWithRoles("mail")).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> jwtAuthenticationProvider.authenticate(authentication));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

}