package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalAuthenticationDelegationServiceTest {

    @InjectMocks
    private ExternalAuthenticationDelegationService externalAuthenticationDelegationService;

    @Mock
    private ExternalAuthenticationValidationService externalAuthenticationValidationService;

    @Mock
    private ExternalAuthenticationFactoryService externalAuthenticationFactoryService;

    @Mock
    private ExternalAuthenticationProviderService externalAuthenticationProviderService;

    @Test
    void givenRequestWhenAuthenticateThenAuthenticate() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);
        final ExternalAuthenticationResult authenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);

        doNothing().when(externalAuthenticationValidationService).validateRequest(request);
        when(externalAuthenticationFactoryService.getProvider(request.getProvider())).thenReturn(externalAuthenticationProviderService);
        when(externalAuthenticationProviderService.authenticate(request)).thenReturn(authenticationResult);

        //when
        final ExternalAuthenticationResult result = externalAuthenticationDelegationService.authenticate(request);

        //then
        assertThat(result).isEqualTo(authenticationResult);
    }

}