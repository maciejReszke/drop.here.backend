package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.authentication.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.authentication.authentication.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountValidationService accountValidationService;
    private final AccountPersistenceService accountPersistenceService;
    private final AccountMappingService accountMappingService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final PasswordEncoder passwordEncoder;
    private final PrivilegeService privilegeService;

    @Transactional
    public LoginResponse createAccount(AccountCreationRequest accountCreationRequest) {
        accountValidationService.validateRequest(accountCreationRequest);
        final String encodedPassword = encodePassword(accountCreationRequest);
        final Account account = accountMappingService.newAccount(accountCreationRequest, encodedPassword);
        accountPersistenceService.createAccount(account);
        privilegeService.addNewAccountPrivileges(account);
        return authenticationExecutiveService.successLogin(account);
    }

    private String encodePassword(AccountCreationRequest accountCreationRequest) {
        return passwordEncoder.encode(accountCreationRequest.getPassword().trim());
    }

    public Optional<Account> findActiveAccountByMail(String mail) {
        return accountPersistenceService.findByMail(mail)
                .filter(account -> account.getAccountStatus() == AccountStatus.ACTIVE);
    }

    public Optional<Account> findActiveAccountByMailWithRoles(String mail) {
        return accountPersistenceService.findByMailWithRoles(mail)
                .filter(account -> account.getAccountStatus() == AccountStatus.ACTIVE);
    }

    public boolean isPasswordValid(Account account, String rawPassword) {
        return passwordEncoder.matches(rawPassword, account.getPassword());
    }
}
