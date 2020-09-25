package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInfoResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountValidationService accountValidationService;

    @Mock
    private AccountPersistenceService accountPersistenceService;

    @Mock
    private AccountMappingService accountMappingService;

    @Mock
    private AuthenticationExecutiveService authenticationExecutiveService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PrivilegeService privilegeService;

    @Test
    void givenValidCreationRequestWhenCreateAccountThenCreate() {
        //given
        final AccountCreationRequest creationRequest = AccountDataGenerator.accountCreationRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final LoginResponse creationResponse = LoginResponse.builder().build();

        when(passwordEncoder.encode(creationRequest.getPassword())).thenReturn("password");

        doNothing().when(accountValidationService).validateRequest(creationRequest);
        when(accountMappingService.newAccount(creationRequest, "password")).thenReturn(account);
        doNothing().when(accountPersistenceService).createAccount(account);
        when(authenticationExecutiveService.successLogin(account)).thenReturn(creationResponse);
        doNothing().when(privilegeService).addNewAccountPrivileges(account);

        //when
        final LoginResponse response = accountService.createAccount(creationRequest);

        //then
        assertThat(response).isEqualTo(creationResponse);
    }

    @Test
    void givenExistingActiveAccountWhenFindActiveByMailThenGet() {
        //given
        final String mail = "mail";
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountPersistenceService.findByMail(mail)).thenReturn(Optional.of(account));

        //when
        final Optional<Account> foundAccount = accountService.findActiveAccountByMail(mail);

        //then
        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.orElseThrow()).isEqualTo(account);
    }

    @Test
    void givenExistingNotActiveAccountWhenFindActiveByMailThenEmpty() {
        //given
        final String mail = "mail";
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAccountStatus(AccountStatus.INACTIVE);

        when(accountPersistenceService.findByMail(mail)).thenReturn(Optional.of(account));

        //when
        final Optional<Account> foundAccount = accountService.findActiveAccountByMail(mail);

        //then
        assertThat(foundAccount).isEmpty();
    }

    @Test
    void givenExistingActiveAccountWhenFindActiveByMailWithRolesThenGet() {
        //given
        final String mail = "mail";
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountPersistenceService.findByMailWithRoles(mail)).thenReturn(Optional.of(account));

        //when
        final Optional<Account> foundAccount = accountService.findActiveAccountByMailWithRoles(mail);

        //then
        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.orElseThrow()).isEqualTo(account);
    }

    @Test
    void givenExistingNotActiveAccountWhenFindActiveByMailWithRolesThenEmpty() {
        //given
        final String mail = "mail";
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAccountStatus(AccountStatus.INACTIVE);

        when(accountPersistenceService.findByMailWithRoles(mail)).thenReturn(Optional.of(account));

        //when
        final Optional<Account> foundAccount = accountService.findActiveAccountByMailWithRoles(mail);

        //then
        assertThat(foundAccount).isEmpty();
    }

    @Test
    void givenMatchingPasswordWhenIsPasswordValidThenTrue() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final String password = "password";
        when(passwordEncoder.matches(password, account.getPassword())).thenReturn(true);

        //when
        final boolean result = accountService.isPasswordValid(account, password);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotMatchingPasswordWhenIsPasswordValidThenFalse() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final String password = "password";
        when(passwordEncoder.matches(password, account.getPassword())).thenReturn(false);

        //when
        final boolean result = accountService.isPasswordValid(account, password);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenAccountAndFirstProfileCreatedWhenAccountProfileCreatedThenSetProfileRegisteredAndSave() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAnyProfileRegistered(false);

        doNothing().when(accountPersistenceService).updateAccount(account);

        //when
        final AccountProfileType result = accountService.addProfile(account);

        //then
        assertThat(result).isEqualTo(AccountProfileType.MAIN);
        assertThat(account.isAnyProfileRegistered()).isTrue();
    }

    @Test
    void givenAccountAndNextProfileCreatedWhenAccountProfileCreatedThenReturnSubprofile() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAnyProfileRegistered(true);

        //when
        final AccountProfileType result = accountService.addProfile(account);

        //then
        assertThat(result).isEqualTo(AccountProfileType.SUBPROFILE);
        assertThat(account.isAnyProfileRegistered()).isTrue();
        verifyNoInteractions(accountPersistenceService);
    }

    @Test
    void givenAccountAuthenticationWhenGetAccountInfoThenGet() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        final AccountInfoResponse accountInfoResponse = AccountInfoResponse.builder().build();
        when(accountMappingService.toAccountInfoResponse(account)).thenReturn(accountInfoResponse);

        //when
        final AccountInfoResponse response = accountService.getAccountInfo(accountAuthentication);

        //then
        assertThat(response).isEqualTo(accountInfoResponse);
    }

    @Test
    void givenExistingAccountWhenExistsByMailThenTrue() {
        //given
        final String mail = "mail@mail.com";

        when(accountPersistenceService.findByMail(mail)).thenReturn(Optional.of(Account.builder().build()));

        //when
        final boolean result = accountService.existsByMail(mail);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotExistingAccountWhenExistsByMailThenFalse() {
        //given
        final String mail = "mail@mail.com";

        when(accountPersistenceService.findByMail(mail)).thenReturn(Optional.empty());

        //when
        final boolean result = accountService.existsByMail(mail);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenExternalAuthenticationResultWhenCreateAccountThenCreate() {
        //given
        final ExternalAuthenticationResult authenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);
        final Account account = Account.builder().build();

        when(accountMappingService.newAccount(authenticationResult)).thenReturn(account);
        doNothing().when(accountPersistenceService).createAccount(account);
        doNothing().when(privilegeService).addNewAccountPrivileges(account);

        //when
        final Account result = accountService.createAccount(authenticationResult);

        //then
        assertThat(result).isEqualTo(account);
    }

    @Test
    void givenWithProfileAccountWhenGetProfileTypeThenGet(){
        //given
        final Account account = Account.builder().isAnyProfileRegistered(true).build();

        //when
        final AccountProfileType profileType = accountService.getProfileType(account);

        //then
        assertThat(profileType).isEqualTo(AccountProfileType.SUBPROFILE);
    }

    @Test
    void givenWithoutProfileAccountWhenGetProfileTypeThenGet(){
        //given
        final Account account = Account.builder().isAnyProfileRegistered(false).build();

        //when
        final AccountProfileType profileType = accountService.getProfileType(account);

        //then
        assertThat(profileType).isEqualTo(AccountProfileType.MAIN);
    }
}