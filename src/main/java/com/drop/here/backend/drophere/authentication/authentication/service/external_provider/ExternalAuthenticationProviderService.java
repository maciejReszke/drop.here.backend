package com.drop.here.backend.drophere.authentication.authentication.service.external_provider;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import reactor.core.publisher.Mono;

public interface ExternalAuthenticationProviderService {
    Mono<ExternalAuthenticationResult> authenticate(ExternalAuthenticationProviderLoginRequest request);
}
