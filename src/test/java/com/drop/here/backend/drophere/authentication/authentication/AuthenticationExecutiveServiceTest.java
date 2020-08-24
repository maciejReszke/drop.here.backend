package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);

        when(jwtService.createToken(account)).thenReturn(new TokenResponse("token", LocalDateTime.of(2000, 1, 1, 1, 1, 1)));

        //when
        final LoginResponse response = authenticationExecutiveService.successLogin(account);

        //then
        assertThat(response.getToken()).isEqualTo("token");
        assertThat(response.getTokenValidUntil()).isEqualTo("2000-01-01T01:01:01");
    }

    @Test
    void givenAccountAuthenticationWithoutProfileWhenGetAuthenticationInfoThenGet() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountAuthentication authentication = AuthenticationDataGenerator.accountAuthentication(account);
        account.setId(1L);

        //when
        final AuthenticationResponse response = authenticationExecutiveService.getAuthenticationInfo(authentication);

        //then
        assertThat(response.getAccountId()).isEqualTo(account.getId());
        assertThat(response.getAccountStatus()).isEqualTo(account.getAccountStatus());
        assertThat(response.getMail()).isEqualTo(account.getMail());
        assertThat(response.getTokenValidUntil()).isEqualTo(authentication.getTokenValidUntil().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(response.getAccountType()).isEqualTo(account.getAccountType());
        assertThat(response.getRoles()).hasSize(1);
        assertThat(response.isHasProfile()).isFalse();
        assertThat(response.isLoggedOnProfile()).isFalse();
        assertThat(response.getRoles().get(0)).isEqualTo("authority");
    }

    @Test
    void givenAccountAuthenticationWithProfileWhenGetAuthenticationInfoThenGet() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        account.setId(1L);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication authentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);

        //when
        final AuthenticationResponse response = authenticationExecutiveService.getAuthenticationInfo(authentication);

        //then
        assertThat(response.getAccountId()).isEqualTo(account.getId());
        assertThat(response.getAccountStatus()).isEqualTo(account.getAccountStatus());
        assertThat(response.getMail()).isEqualTo(account.getMail());
        assertThat(response.getTokenValidUntil()).isEqualTo(authentication.getTokenValidUntil().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(response.getAccountType()).isEqualTo(account.getAccountType());
        assertThat(response.getRoles()).hasSize(1);
        assertThat(response.getRoles().get(0)).isEqualTo("authority");
        assertThat(response.isHasProfile()).isTrue();
        assertThat(response.isLoggedOnProfile()).isTrue();
        assertThat(response.getProfileUid()).isEqualTo(accountProfile.getProfileUid());
        assertThat(response.getProfileFirstName()).isEqualTo(accountProfile.getFirstName());
        assertThat(response.getProfileLastName()).isEqualTo(accountProfile.getLastName());
        assertThat(response.getProfileType()).isEqualTo(accountProfile.getProfileType());
    }

}