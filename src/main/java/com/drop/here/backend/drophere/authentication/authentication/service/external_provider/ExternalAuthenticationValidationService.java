package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.enums.ExternalAuthenticationProvider;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

// TODO MONO:
@Service
public class ExternalAuthenticationValidationService {

    public void validateRequest(ExternalAuthenticationProviderLoginRequest request) {
        Try.ofSupplier(() -> ExternalAuthenticationProvider.valueOf(request.getProvider()))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                        "During external authentication given invalid provider %s", request.getProvider()),
                        RestExceptionStatusCode.LOGIN_PROVIDER_INVALID_LOGIN_PROVIDER));
    }
}
