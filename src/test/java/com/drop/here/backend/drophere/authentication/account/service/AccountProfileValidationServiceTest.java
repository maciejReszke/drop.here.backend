package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class AccountProfileValidationServiceTest {

    @InjectMocks
    private AccountProfileValidationService accountProfileValidationService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(accountProfileValidationService, "minimalPasswordLength", 8, true);
    }

    @Test
    void givenValidRequestWhenValidateRequestThenDoNothing() {
        //given
        final AccountProfileCreationRequest request = AccountProfileDataGenerator.accountProfileRequest(1);
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);

        //when
        accountProfileValidationService.validateRequest(request, account);

        //then
        assertThat(true).isTrue();
    }

    @Test
    void givenInvalidTypeValidRequestWhenValidateRequestThenError() {
        //given
        final AccountProfileCreationRequest request = AccountProfileDataGenerator.accountProfileRequest(1);
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        account.setAccountType(AccountType.CUSTOMER);

//when
        final Throwable throwable = catchThrowable(() -> accountProfileValidationService.validateRequest(request, account));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_ACCOUNT_TYPE_ACCOUNT_PROFILE_CREATION.ordinal());
    }

    @Test
    void givenTooShortPasswordWhenValidateRequestThenError() {
        //given
        final AccountProfileCreationRequest request = AccountProfileDataGenerator.accountProfileRequest(1);
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        request.setPassword("abc");

        //when
        final Throwable throwable = catchThrowable(() -> accountProfileValidationService.validateRequest(request, account));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_PROFILE_CREATION_PASSWORD_LENGTH.ordinal());
    }

}