package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInformationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.authentication.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.authentication.authentication.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountValidationService accountValidationService;
    private final AccountPersistenceService accountPersistenceService;
    private final AccountMappingService accountMappingService;
    private final AuthenticationExecutiveService authenticationExecutiveService;

    public LoginResponse createAccount(AccountCreationRequest accountCreationRequest) {
        accountValidationService.validateRequest(accountCreationRequest);
        final Account account = accountMappingService.newAccount(accountCreationRequest);
        accountPersistenceService.createAccount(account);
        return authenticationExecutiveService.successLogin(account);
    }

    public AccountInformationResponse getAccountInformation(Account account) {
        return accountMappingService.toAccountInformationResponse(account);
    }

    public Optional<Account> findActiveAccountByMail(String mail) {
        return accountPersistenceService.findByMail(mail)
                .filter(account -> account.getAccountStatus() == AccountStatus.ACTIVE);
    }

    // TODO: 03/08/2020
    public boolean isPasswordValid(Account account, String rawPassword) {
        return false;
    }
}
