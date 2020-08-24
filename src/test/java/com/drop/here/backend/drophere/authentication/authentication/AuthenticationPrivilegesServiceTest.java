package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthenticationPrivilegesServiceTest {

    @InjectMocks
    private AuthenticationPrivilegesService authenticationPrivilegesService;

    @Test
    void givenOwnAccountOperationWhenIsOwnAccountOperationThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        account.setId(1L);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService.isOwnAccountOperation(accountAuthentication, 1L);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotOwnAccountOperationWhenIsOwnAccountOperationThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        account.setId(1L);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService.isOwnAccountOperation(accountAuthentication, 2L);

        //then
        assertThat(result).isFalse();
    }


    @Test
    void givenOwnAccountProfileOperationWhenIsOwnProfileOperationThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);

        //when
        final boolean result = authenticationPrivilegesService.isOwnProfileOperation(accountAuthentication, accountProfile.getProfileUid());

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNullProfileOperationWhenIsOwnProfileOperationThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService.isOwnProfileOperation(accountAuthentication, "aaa");

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenNotOwnAccountProfileOperationWhenIsOwnProfileOperationThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);

        //when
        final boolean result = authenticationPrivilegesService.isOwnProfileOperation(accountAuthentication,
                accountProfile.getProfileUid() + "aaa");

        //then
        assertThat(result).isFalse();
    }
}