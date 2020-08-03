package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInformationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.authentication.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.authentication.authentication.LoginResponse;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
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

    @Test
    void givenValidCreationRequestWhenCreateAccountThenCreate() {
        //given
        final AccountCreationRequest creationRequest = AccountDataGenerator.accountCreationRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final LoginResponse creationResponse = LoginResponse.builder().build();

        doNothing().when(accountValidationService).validateRequest(creationRequest);
        when(accountMappingService.newAccount(creationRequest)).thenReturn(account);
        doNothing().when(accountPersistenceService).createAccount(account);
        when(authenticationExecutiveService.successLogin(account)).thenReturn(creationResponse);

        //when
        final LoginResponse response = accountService.createAccount(creationRequest);

        //then
        assertThat(response).isEqualTo(creationResponse);
    }

    @Test
    void givenAccountWhenGetAccountInformationThenGet() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);

        final AccountInformationResponse accountInformationResponse = AccountInformationResponse.builder().build();
        when(accountMappingService.toAccountInformationResponse(account)).thenReturn(accountInformationResponse);

        //when
        final AccountInformationResponse response = accountService.getAccountInformation(account);

        //then
        assertThat(response).isEqualTo(accountInformationResponse);
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

}