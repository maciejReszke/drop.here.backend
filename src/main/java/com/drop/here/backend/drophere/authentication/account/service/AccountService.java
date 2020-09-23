package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInfoResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountValidationService accountValidationService;
    private final AccountPersistenceService accountPersistenceService;
    private final AccountMappingService accountMappingService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final PasswordEncoder passwordEncoder;
    private final PrivilegeService privilegeService;

    public Mono<Account> createAccount(ExternalAuthenticationResult result) {
        final Account account = accountMappingService.newAccount(result);
        privilegeService.addNewAccountPrivileges(account);
        return accountPersistenceService.createAccount(account);
    }

    public Mono<LoginResponse> createAccount(AccountCreationRequest accountCreationRequest) {
        return accountValidationService.validateRequest(accountCreationRequest)
                .map(request -> accountMappingService.newAccount(request, encodePassword(accountCreationRequest)))
                .doOnNext(privilegeService::addNewAccountPrivileges)
                .flatMap(accountPersistenceService::createAccount)
                .map(authenticationExecutiveService::successLogin);
    }

    private String encodePassword(AccountCreationRequest accountCreationRequest) {
        return passwordEncoder.encode(accountCreationRequest.getPassword().trim());
    }

    public Mono<Account> findActiveAccountByMail(String mail) {
        return accountPersistenceService.findByMail(mail)
                .filter(account -> account.getAccountStatus() == AccountStatus.ACTIVE);
    }

    public boolean isPasswordValid(Account account, String rawPassword) {
        return passwordEncoder.matches(rawPassword, account.getPassword());
    }

    // TODO: 23/09/2020 test
    public AccountProfileType getProfileType(Account account, AccountProfile accountProfile) {
        return account.isAnyProfileRegistered()
                ? AccountProfileType.SUBPROFILE
                : AccountProfileType.MAIN;
    }

    public Mono<AccountProfileType> addProfile(Account account, AccountProfile accountProfile) {
        if (account.isAnyProfileRegistered()) {
            return Mono.just(AccountProfileType.SUBPROFILE);
        }
        account.setAnyProfileRegistered(true);
        return accountPersistenceService.updateAccount(account)
                .thenReturn(AccountProfileType.MAIN);
    }

    public Mono<AccountInfoResponse> getAccountInfo(AccountAuthentication accountAuthentication) {
        return accountMappingService.toAccountInfoResponse(accountAuthentication.getPrincipal());
    }

    public Mono<Boolean> existsByMail(String email) {
        return accountPersistenceService.findByMail(email)
                .map(ignore -> true);
    }
}
