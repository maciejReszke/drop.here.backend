package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountValidationServiceTest {
    @InjectMocks
    private AccountValidationService accountValidationService;

    @Mock
    private AccountPersistenceService accountPersistenceService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(accountValidationService, "minimalPasswordLength", 8, true);
    }

    @Test
    void givenValidRequestWhenValidateRequestThenDoNothing() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);

        when(accountPersistenceService.findByMail(request.getMail())).thenReturn(Optional.empty());

        //when
        accountValidationService.validateRequest(request);

        //then
        assertThat(true).isTrue();
    }

    @Test
    void givenTooShortPasswordWhenValidateRequestThenError() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        request.setPassword("abc");

        //when
        final Throwable throwable = catchThrowable(() -> accountValidationService.validateRequest(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_PASSWORD_LENGTH.ordinal());
    }

    @Test
    void givenInvalidAccountTypeWhenValidateRequestThenError() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        request.setAccountType(AccountType.COMPANY + "aa");

        //when
        final Throwable throwable = catchThrowable(() -> accountValidationService.validateRequest(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_ACCOUNT_TYPE.ordinal());
    }

    @Test
    void givenExistingMailWhenValidateRequestThenError() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        when(accountPersistenceService.findByMail(request.getMail())).thenReturn(Optional.of(Account.builder().build()));

        //when
        final Throwable throwable = catchThrowable(() -> accountValidationService.validateRequest(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_MAIL_EXISTS.ordinal());
    }

}