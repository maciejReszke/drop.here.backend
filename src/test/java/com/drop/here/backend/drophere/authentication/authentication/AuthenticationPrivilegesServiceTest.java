package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationPrivilegesService;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.company.CompanyService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationPrivilegesServiceTest {

    @InjectMocks
    private AuthenticationPrivilegesService authenticationPrivilegesService;

    @Mock
    private CompanyService companyService;

    @Test
    void givenOwnAccountOperationWhenIsOwnAccountOperationThenTrue() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
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
        final Account account = AccountDataGenerator.companyAccount(1);
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
        final Account account = AccountDataGenerator.companyAccount(1);
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
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService.isOwnProfileOperation(accountAuthentication, "aaa");

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenNotOwnAccountProfileOperationWhenIsOwnProfileOperationThenFalse() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);

        //when
        final boolean result = authenticationPrivilegesService.isOwnProfileOperation(accountAuthentication,
                accountProfile.getProfileUid() + "aaa");

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenNotCompanyAccountWhenIsOwnCompanyOperationThenFalse() {
        //given
        final Company company = Company.builder().uid("uid").build();
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setCompany(company);
        account.setAccountType(AccountType.CUSTOMER);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService.isOwnCompanyOperation(accountAuthentication, "uid");

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenLackOfCompanyAccountWhenIsOwnCompanyOperationThenFalse() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAccountType(AccountType.COMPANY);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService.isOwnCompanyOperation(accountAuthentication, "uid");

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenDifferentCompanyUidAccountWhenIsOwnCompanyOperationThenFalse() {
        //given
        final Company company = Company.builder().uid("uid1").build();
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setCompany(company);
        account.setAccountType(AccountType.COMPANY);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService
                .isOwnCompanyOperation(accountAuthentication, "uid");

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenValidSameCompanyAccountWhenIsOwnCompanyOperationThenTrue() {
        //given
        final Company company = Company.builder().uid("uid").build();
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setCompany(company);
        account.setAccountType(AccountType.COMPANY);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        //when
        final boolean result = authenticationPrivilegesService
                .isOwnCompanyOperation(accountAuthentication, "uid");

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenVisibleCompanyWhenIsCompanyVisibleThenTrue() {
        //given
        final String companyUid = "companyUid";

        when(companyService.isVisible(companyUid)).thenReturn(true);

        //when
        final boolean result = authenticationPrivilegesService.isCompanyVisible(companyUid);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotVisibleCompanyWhenIsCompanyVisibleThenFalse() {
        //given
        final String companyUid = "companyUid";

        when(companyService.isVisible(companyUid)).thenReturn(false);

        //when
        final boolean result = authenticationPrivilegesService.isCompanyVisible(companyUid);

        //then
        assertThat(result).isFalse();
    }
}