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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
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
    void givenValidRequestWhenValidateRequestThenReturnRequest() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);

        when(accountPersistenceService.findByMail(request.getMail())).thenReturn(Mono.empty());

        //when
        final Mono<AccountCreationRequest> result = accountValidationService.validateRequest(request);

        //then
        StepVerifier.create(result)
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    void givenTooShortPasswordWhenValidateRequestThenError() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        request.setPassword("abc");

        //when

        //when
        final Mono<AccountCreationRequest> result = accountValidationService.validateRequest(request);

        //then
        StepVerifier.create(result)
                .consumeErrorWith(throwable -> {
                    assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
                    assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_PASSWORD_LENGTH.ordinal());
                })
                .verify();
    }

    @Test
    void givenInvalidAccountTypeWhenValidateRequestThenError() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        request.setAccountType(AccountType.COMPANY + "aa");

        //when
        final Mono<AccountCreationRequest> result = accountValidationService.validateRequest(request);

        //then
        StepVerifier.create(result)
                .consumeErrorWith(throwable -> {
                    assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
                    assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_ACCOUNT_TYPE.ordinal());
                })
                .verify();
    }

    @Test
    void givenExistingMailWhenValidateRequestThenError() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        when(accountPersistenceService.findByMail(request.getMail())).thenReturn(Mono.just(Account.builder().build()));

        //when
        final Mono<AccountCreationRequest> result = accountValidationService.validateRequest(request);

        //then
        StepVerifier.create(result)
                .consumeErrorWith(throwable -> {
                    assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
                    assertThat(((RestIllegalRequestValueException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_MAIL_EXISTS.ordinal());
                })
                .verify();
    }

}