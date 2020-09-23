package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExternalAuthenticationDelegationService {
    private final ExternalAuthenticationValidationService externalAuthenticationValidationService;
    private final ExternalAuthenticationFactoryService externalAuthenticationFactoryService;

    public Mono<ExternalAuthenticationResult> authenticate(ExternalAuthenticationProviderLoginRequest request) {
        externalAuthenticationValidationService.validateRequest(request);
        return externalAuthenticationFactoryService.getProvider(request.getProvider())
                .authenticate(request);
    }
}
