package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.enums.ExternalAuthenticationProvider;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.FacebookExternalAuthenticationProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExternalAuthenticationFactoryServiceTest {
    @InjectMocks
    private ExternalAuthenticationFactoryService externalAuthenticationFactoryService;

    @Mock
    private FacebookExternalAuthenticationProviderService facebookExternalAuthenticationProviderService;

    @Test
    void givenFacebookProviderWhenGetProviderThenGetFacebook() {
        //given
        final String providerName = ExternalAuthenticationProvider.FACEBOOK.name();

        //when
        final ExternalAuthenticationProviderService provider = externalAuthenticationFactoryService.getProvider(providerName);

        //then
        assertThat(provider).isEqualTo(facebookExternalAuthenticationProviderService);
    }
}