package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class ExternalAuthenticationValidationServiceTest {

    @InjectMocks
    private ExternalAuthenticationValidationService externalAuthenticationValidationService;

    @Test
    void givenValidRequestWhenValidateRequestThenDoNothing() {
        //given
        final ExternalAuthenticationProviderLoginRequest externalAuthenticationProviderLoginRequest =
                ExternalAuthenticationDataGenerator.facebook(1);

        //when
        final Throwable throwable = catchThrowable(() -> externalAuthenticationValidationService.validateRequest(externalAuthenticationProviderLoginRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidRequestProviderWhenValidateRequestThenError() {
        //given
        final ExternalAuthenticationProviderLoginRequest externalAuthenticationProviderLoginRequest =
                ExternalAuthenticationDataGenerator.facebook(1);

        externalAuthenticationProviderLoginRequest.setProvider("CALGON");

        //when
        final Throwable throwable = catchThrowable(() -> externalAuthenticationValidationService.validateRequest(externalAuthenticationProviderLoginRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

}