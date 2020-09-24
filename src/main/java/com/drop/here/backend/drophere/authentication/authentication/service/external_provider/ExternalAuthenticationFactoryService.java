package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.enums.ExternalAuthenticationProvider;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.FacebookExternalAuthenticationProviderService;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class ExternalAuthenticationFactoryService {
    private final FacebookExternalAuthenticationProviderService facebookProvider;

    public ExternalAuthenticationProviderService getProvider(String provider) {
        return API.Match(ExternalAuthenticationProvider.valueOf(provider)).of(
                Case($(ExternalAuthenticationProvider.FACEBOOK), facebookProvider),
                Case($(), () -> invalidProvider(provider))
        );
    }

    private ExternalAuthenticationProviderService invalidProvider(String provider) {
        throw new RestIllegalRequestValueException(String.format(
                "Invalid external authentication provider service %s during fetching provider", provider),
                RestExceptionStatusCode.LOGIN_PROVIDER_INVALID_PROVIDER_REQUEST
        );
    }
}
