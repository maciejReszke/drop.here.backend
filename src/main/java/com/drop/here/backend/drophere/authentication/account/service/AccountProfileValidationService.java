package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccountProfileValidationService {

    @Value("${accountCreation.minimalPasswordLength}")
    private int minimalPasswordLength;

    public void validateRequest(AccountProfileCreationRequest accountProfileRequest, Account account) {
        validatePassword(accountProfileRequest.getPassword(), account);
        validateType(account);
    }

    private void validateType(Account account) {
        if (account.getAccountType() != AccountType.COMPANY) {
            throw new RestIllegalRequestValueException(String.format("During profile creation profile type must be COMPANY but was %s", account.getAccountType()),
                    RestExceptionStatusCode.INVALID_ACCOUNT_TYPE_ACCOUNT_PROFILE_CREATION);
        }
    }

    private void validatePassword(String password, Account account) {
        final String trimmedPassword = password.trim();
        if (trimmedPassword.length() < minimalPasswordLength) {
            throw new RestIllegalRequestValueException(String.format("Password must be at least %s long but was %s for account profile request with account id %s", minimalPasswordLength, trimmedPassword.length(), account.getId()),
                    RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_PROFILE_CREATION_PASSWORD_LENGTH);
        }
    }
}