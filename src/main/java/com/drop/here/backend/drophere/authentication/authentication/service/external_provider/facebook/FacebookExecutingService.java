package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.exception.UnauthorizedRestException;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAccessToken;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAccessTokenResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAuthenticationDataResponse;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Objects;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class FacebookExecutingService {
    // TODO: 23/09/2020
    private final RestTemplate restTemplate;
    private final FacebookConfiguration facebookConfiguration;

    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String CLIENT_SECRET_PARAM = "client_secret";
    private static final String USER_CODE_PARAM = "code";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String FIELDS_PARAM = "fields";
    private static final String FIELD_DELIMITER = ",";

    public Mono<FacebookAccessToken> exchangeToken(ExternalAuthenticationProviderLoginRequest request) {
        final String url = buildExchangeTokenUrl(request);
        try {
            final ResponseEntity<FacebookAccessTokenResponse> response = restTemplate.getForEntity(url, FacebookAccessTokenResponse.class);
            return new FacebookAccessToken(handleExchangeTokenResponse(response));
        } catch (HttpClientErrorException clientErrorException) {
            throw new UnauthorizedRestException(String.format("Failed to exchange facebook token, 4xx status %s", clientErrorException.getMessage()), RestExceptionStatusCode.LOGIN_FACEBOOK_EXCHANGE_TOKEN_FAILURE);
        }
    }

    private String buildExchangeTokenUrl(ExternalAuthenticationProviderLoginRequest request) {
        return UriComponentsBuilder.fromUriString(facebookConfiguration.getExchangeTokenBaseUrl())
                .queryParam(CLIENT_ID_PARAM, facebookConfiguration.getClientId())
                .queryParam(CLIENT_SECRET_PARAM, facebookConfiguration.getClientSecret())
                .queryParam(REDIRECT_URI_PARAM, request.getRedirectUri())
                .queryParam(USER_CODE_PARAM, request.getCode())
                .build()
                .toUriString();
    }

    private String handleExchangeTokenResponse(ResponseEntity<FacebookAccessTokenResponse> response) {
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && StringUtils.isNotBlank(response.getBody().getAccessToken())) {
            return response.getBody().getAccessToken();
        }
        throw new UnauthorizedRestException(String.format("Failed to exchange facebook token, status is %s", response.getStatusCode()),
                RestExceptionStatusCode.LOGIN_FACEBOOK_EXCHANGE_TOKEN_FAILURE);
    }

    public Mono<ExternalAuthenticationResult> fetchAuthenticationData(FacebookAccessToken accessToken) {
        final String url = buildFetchAuthenticationDataUrl(accessToken);
        try {
            final ResponseEntity<FacebookAuthenticationDataResponse> response = restTemplate.getForEntity(url, FacebookAuthenticationDataResponse.class);
            return handleFetchAuthenticationDataResponse(response);
        } catch (HttpClientErrorException clientErrorException) {
            throw new UnauthorizedRestException(String.format("Failed to fetch facebook authentication data, status 4xx %s", clientErrorException.getMessage()), RestExceptionStatusCode.LOGIN_FACEBOOK_FETCH_DATA_FAILURE);
        }
    }

    private ExternalAuthenticationResult handleFetchAuthenticationDataResponse(ResponseEntity<FacebookAuthenticationDataResponse> response) {
        final FacebookAuthenticationDataResponse body = response.getBody();
        if (isCorrectAuthenticationDataResponse(response, body)) {
            return mapToAuthenticationResultWithMaybeFetchedPicture(Objects.requireNonNull(response.getBody()));
        }
        throw new UnauthorizedRestException(String.format("Failed to fetch authentication data, status is %s", response.getStatusCode()),
                RestExceptionStatusCode.LOGIN_FACEBOOK_FETCH_DATA_FAILURE);
    }

    private boolean isCorrectAuthenticationDataResponse(ResponseEntity<FacebookAuthenticationDataResponse> response, FacebookAuthenticationDataResponse body) {
        return response.getStatusCode() == HttpStatus.OK &&
                body != null &&
                StringUtils.isNoneBlank(body.getEmail(), body.getFirstName(), body.getLastName());
    }

    private ExternalAuthenticationResult mapToAuthenticationResultWithMaybeFetchedPicture(FacebookAuthenticationDataResponse body) {
        final byte[] picture = StringUtils.isNotBlank(body.getPictureUrl())
                ? fetchPicture(body.getPictureUrl())
                : null;
        return ExternalAuthenticationResult
                .builder()
                .image(picture)
                .lastName(body.getLastName().trim())
                .firstName(body.getFirstName().trim())
                .email(body.getEmail().trim())
                .build();
    }

    private byte[] fetchPicture(String pictureUrl) {
        return restTemplate.getForObject(pictureUrl, byte[].class);
    }

    private String buildFetchAuthenticationDataUrl(FacebookAccessToken accessToken) {
        final String fields = String.join(FIELD_DELIMITER, facebookConfiguration.getFetchAuthenticationDataWantedFields());
        return UriComponentsBuilder.fromUriString(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .queryParam(ACCESS_TOKEN_PARAM, accessToken.getToken())
                .queryParam(FIELDS_PARAM, fields)
                .build()
                .toUriString();
    }
}
