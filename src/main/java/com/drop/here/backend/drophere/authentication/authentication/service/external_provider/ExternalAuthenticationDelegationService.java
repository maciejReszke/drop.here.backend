package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalAuthenticationDelegationService {
    private final ExternalAuthenticationValidationService externalAuthenticationValidationService;
    private final ExternalAuthenticationFactoryService externalAuthenticationFactoryService;

    public ExternalAuthenticationResult authenticate(ExternalAuthenticationProviderLoginRequest request) {
        externalAuthenticationValidationService.validateRequest(request);
        return externalAuthenticationFactoryService.getProvider(request.getProvider())
                .authenticate(request);
    }
}
