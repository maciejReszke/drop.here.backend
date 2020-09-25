package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.ExternalAuthenticationProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookExternalAuthenticationProviderService implements ExternalAuthenticationProviderService {
    private final FacebookExecutingService facebookExecutingService;

    @Override
    public Mono<ExternalAuthenticationResult> authenticate(ExternalAuthenticationProviderLoginRequest request) {
        return facebookExecutingService.exchangeToken(request)
                .flatMap(facebookExecutingService::fetchAuthenticationData)
                .doOnNext(ignore -> log.info("Successfully authenticated with facebook user"));
    }
}
