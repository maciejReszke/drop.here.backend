package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.ExternalAuthenticationProviderService;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookExternalAuthenticationProviderService implements ExternalAuthenticationProviderService {
    private final FacebookExecutingService facebookExecutingService;

    @Override
    public ExternalAuthenticationResult authenticate(ExternalAuthenticationProviderLoginRequest request) {
        final FacebookAccessToken accessToken = facebookExecutingService.exchangeToken(request);
        final ExternalAuthenticationResult result = facebookExecutingService.fetchAuthenticationData(accessToken);
        log.info("Successfully authenticated with facebook user with mail {}", result.getEmail());
        return result;
    }
}
