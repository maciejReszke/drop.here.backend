package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountValidationService {
    private final AccountPersistenceService accountPersistenceService;

    @Value("${accountCreation.minimalPasswordLength}")
    private int minimalPasswordLength;

    public void validateRequest(AccountCreationRequest accountCreationRequest) {
        final String mail = accountCreationRequest.getMail();
        validatePassword(accountCreationRequest.getPassword(), mail);
        validateAccountType(accountCreationRequest.getAccountType(), mail);
        validateMail(mail);
    }

    private void validateMail(String mail) {
        if (accountPersistenceService.findByMail(mail).isPresent()) {
            throw new RestIllegalRequestValueException(String.format(
                    "Account with mail %s already exists", mail),
                    RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_MAIL_EXISTS);
        }
    }

    private void validateAccountType(String accountType, String mail) {
        Try.of(() -> AccountType.parseIgnoreCase(accountType))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                        "Invalid account type %s was given during account creation for request with mail %s", accountType, mail),
                        RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_ACCOUNT_TYPE));
    }

    private void validatePassword(String password, String mail) {
        final String trimmedPassword = password.trim();
        if (trimmedPassword.length() < minimalPasswordLength) {
            throw new RestIllegalRequestValueException(String.format("Password must be at least %s long but was %s for request with mail %s", minimalPasswordLength, trimmedPassword.length(), mail),
                    RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_PASSWORD_LENGTH);
        }
    }
}
