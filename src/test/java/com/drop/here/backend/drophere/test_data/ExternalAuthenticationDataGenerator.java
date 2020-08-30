package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import lombok.experimental.UtilityClass;

import static com.drop.here.backend.drophere.authentication.authentication.enums.ExternalAuthenticationProvider.FACEBOOK;

@UtilityClass
public class ExternalAuthenticationDataGenerator {

    public ExternalAuthenticationProviderLoginRequest facebook(int i) {
        return ExternalAuthenticationProviderLoginRequest.builder()
                .code("authenticationCode" + i)
                .provider(FACEBOOK.name())
                .redirectUri("http://localhost:8081/redirectUri" + i)
                .build();
    }

    public ExternalAuthenticationResult externalAuthenticationResult(int i) {
        return ExternalAuthenticationResult.builder()
                .email("email" + i + "@email.pl")
                .firstName("firstName" + i)
                .lastName("lastName" + i)
                .image("image".getBytes())
                .build();
    }
}
