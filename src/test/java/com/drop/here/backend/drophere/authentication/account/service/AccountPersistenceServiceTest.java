package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountPersistenceServiceTest {

    @InjectMocks
    private AccountPersistenceService accountPersistenceService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void givenAccountWhenSaveThenSave() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        when(accountRepository.save(account)).thenReturn(account);

        //when
        accountPersistenceService.createAccount(account);

        //then
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void givenAccountWhenUpdateThenSave() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        when(accountRepository.save(account)).thenReturn(account);

        //when
        accountPersistenceService.updateAccount(account);

        //then
        verifyNoMoreInteractions(accountRepository);
    }

}