package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAccessToken;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacebookExternalAuthenticationProviderServiceTest {

    @InjectMocks
    private FacebookExternalAuthenticationProviderService authenticationProviderService;

    @Mock
    private FacebookExecutingService facebookExecutingService;

    @Test
    void givenAuthenticationRequestWhenAuthenticateThenAuthenticate() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);
        final ExternalAuthenticationResult externalAuthenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);

        final FacebookAccessToken token = new FacebookAccessToken("token");
        when(facebookExecutingService.exchangeToken(request)).thenReturn(Mono.just(token));
        when(facebookExecutingService.fetchAuthenticationData(token)).thenReturn(Mono.just(externalAuthenticationResult));

        //when
        final Mono<ExternalAuthenticationResult> result = authenticationProviderService.authenticate(request);

        //then
        StepVerifier.create(result)
                .expectNext(externalAuthenticationResult)
                .verifyComplete();
    }

}