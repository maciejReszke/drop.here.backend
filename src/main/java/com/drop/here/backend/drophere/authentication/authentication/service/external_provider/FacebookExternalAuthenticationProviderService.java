package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import org.springframework.stereotype.Service;

@Service
public class FacebookExternalAuthenticationProviderService implements ExternalAuthenticationProviderService {

    // TODO: 29/08/2020 test, implement
    @Override
    public ExternalAuthenticationResult authenticate(ExternalAuthenticationProviderLoginRequest request) {
        return null;
    }
}
