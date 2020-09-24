package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountValidationService {
    private final AccountPersistenceService accountPersistenceService;

    @Value("${account-creation.minimal-password-length}")
    private int minimalPasswordLength;

    public Mono<AccountCreationRequest> validateRequest(AccountCreationRequest accountCreationRequest) {
        return validatePassword(accountCreationRequest)
                .flatMap(request -> validateAccountType(accountCreationRequest))
                .flatMap(this::validateMail);
    }

    private Mono<AccountCreationRequest> validateMail(AccountCreationRequest request) {
        final String mail = request.getMail();
        return accountPersistenceService.findByMail(mail)
                .flatMap(account -> Mono.error(new RestIllegalRequestValueException(String.format(
                        "Account with mail %s already exists", mail),
                        RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_MAIL_EXISTS)))
                .map(ignore -> request)
                .switchIfEmpty(Mono.just(request));
    }

    private Mono<AccountCreationRequest> validateAccountType(AccountCreationRequest request) {
        final String mail = request.getMail();
        return Try.of(() -> AccountType.parseIgnoreCase(request.getAccountType()))
                .map(type -> Mono.just(request))
                .getOrElseGet(error -> Mono.error(() -> new RestIllegalRequestValueException(String.format(
                        "Invalid account type %s was given during account creation for request with mail %s", request.getAccountType(), mail),
                        RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_ACCOUNT_TYPE)));
    }

    private Mono<AccountCreationRequest> validatePassword(AccountCreationRequest accountCreationRequest) {
        final String trimmedPassword = accountCreationRequest.getPassword().trim();
        final String mail = accountCreationRequest.getMail();
        return trimmedPassword.length() < minimalPasswordLength
                ? Mono.error(() -> new RestIllegalRequestValueException(String.format("Password must be at least %s long but was %s for request with mail %s", minimalPasswordLength, trimmedPassword.length(), mail),
                RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_PASSWORD_LENGTH))
                : Mono.just(accountCreationRequest);
    }
}
