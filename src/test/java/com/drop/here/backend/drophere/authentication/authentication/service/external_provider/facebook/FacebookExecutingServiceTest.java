package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook;

import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.exception.UnauthorizedRestException;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAccessToken;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAccessTokenResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto.FacebookAuthenticationDataResponse;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacebookExecutingServiceTest {

    @InjectMocks
    private FacebookExecutingService facebookExecutingService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FacebookConfiguration facebookConfiguration;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Test
    void givenValidRequestProcessWhenExchangeTokenThenReturnToken() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);

        when(facebookConfiguration.getExchangeTokenBaseUrl()).thenReturn("http://localhost:80/token");
        when(facebookConfiguration.getClientId()).thenReturn("clientId");
        when(facebookConfiguration.getClientSecret()).thenReturn("clientSecret");

        when(restTemplate.getForEntity(anyString(), eq(FacebookAccessTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(new FacebookAccessTokenResponse("token")));

        //when
        final FacebookAccessToken token = facebookExecutingService.exchangeToken(request);

        //then
        assertThat(token.getToken()).isEqualTo("token");

        verify(restTemplate).getForEntity(urlCaptor.capture(), eq(FacebookAccessTokenResponse.class));
        final String value = urlCaptor.getValue();

        assertThat(value).isEqualTo("http://localhost:80/token?client_id=clientId&client_secret=clientSecret&redirect_uri=http://localhost:8081/redirectUri1&code=authenticationCode1");
    }

    @Test
    void givenInvalidResponseStatusWhenExchangeTokenThenError() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);

        when(facebookConfiguration.getExchangeTokenBaseUrl()).thenReturn("http://localhost:80/token");
        when(facebookConfiguration.getClientId()).thenReturn("clientId");
        when(facebookConfiguration.getClientSecret()).thenReturn("clientSecret");

        when(restTemplate.getForEntity(anyString(), eq(FacebookAccessTokenResponse.class)))
                .thenReturn(ResponseEntity.notFound().build());

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.exchangeToken(request));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenEmptyResponseWhenExchangeTokenThenError() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);

        when(facebookConfiguration.getExchangeTokenBaseUrl()).thenReturn("http://localhost:80/token");
        when(facebookConfiguration.getClientId()).thenReturn("clientId");
        when(facebookConfiguration.getClientSecret()).thenReturn("clientSecret");

        when(restTemplate.getForEntity(anyString(), eq(FacebookAccessTokenResponse.class)))
                .thenReturn(ResponseEntity.ok().build());

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.exchangeToken(request));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenEmptyTokenWhenExchangeTokenThenError() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);

        when(facebookConfiguration.getExchangeTokenBaseUrl()).thenReturn("http://localhost:80/token");
        when(facebookConfiguration.getClientId()).thenReturn("clientId");
        when(facebookConfiguration.getClientSecret()).thenReturn("clientSecret");

        when(restTemplate.getForEntity(anyString(), eq(FacebookAccessTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(new FacebookAccessTokenResponse(null)));

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.exchangeToken(request));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenErrorWhenExchangeTokenThenError() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);

        when(facebookConfiguration.getExchangeTokenBaseUrl()).thenReturn("http://localhost:80/token");
        when(facebookConfiguration.getClientId()).thenReturn("clientId");
        when(facebookConfiguration.getClientSecret()).thenReturn("clientSecret");

        when(restTemplate.getForEntity(anyString(), eq(FacebookAccessTokenResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.CREATED));

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.exchangeToken(request));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenAccessTokenProfileWithPictureWhenFetchAuthenticationDataThenGetExternalAuthenticationResult() {
        //given
        final FacebookAccessToken facebookAccessToken = new FacebookAccessToken("token");
        final FacebookAuthenticationDataResponse response =
                new FacebookAuthenticationDataResponse("email", "firstName", "lastName", "pictureUrl");
        final byte[] picture = "picture".getBytes();

        when(facebookConfiguration.getFetchAuthenticationDataWantedFields())
                .thenReturn("email,first_name,last_name,picture".split(","));
        when(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .thenReturn("https://graph.facebook.com/me");
        when(restTemplate.getForEntity(anyString(), eq(FacebookAuthenticationDataResponse.class)))
                .thenReturn(ResponseEntity.ok(response));
        when(restTemplate.getForObject("pictureUrl", byte[].class)).thenReturn(picture);

        //when
        final ExternalAuthenticationResult result = facebookExecutingService.fetchAuthenticationData(facebookAccessToken);

        //then
        assertThat(result.getEmail()).isEqualTo(response.getEmail());
        assertThat(result.getImage()).isEqualTo(picture);
        assertThat(result.getFirstName()).isEqualTo(response.getFirstName());
        assertThat(result.getLastName()).isEqualTo(response.getLastName());
    }

    @Test
    void givenAccessTokenProfileWithoutPictureWhenFetchAuthenticationDataThenGetExternalAuthenticationResult() {
        //given
        final FacebookAccessToken facebookAccessToken = new FacebookAccessToken("token");
        final FacebookAuthenticationDataResponse response =
                new FacebookAuthenticationDataResponse("email", "firstName", "lastName", null);

        when(facebookConfiguration.getFetchAuthenticationDataWantedFields())
                .thenReturn("email,first_name,last_name,picture".split(","));
        when(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .thenReturn("https://graph.facebook.com/me");
        when(restTemplate.getForEntity(anyString(), eq(FacebookAuthenticationDataResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        //when
        final ExternalAuthenticationResult result = facebookExecutingService.fetchAuthenticationData(facebookAccessToken);

        //then
        assertThat(result.getEmail()).isEqualTo(response.getEmail());
        assertThat(result.getImage()).isNull();
        assertThat(result.getFirstName()).isEqualTo(response.getFirstName());
        assertThat(result.getLastName()).isEqualTo(response.getLastName());
    }

    @Test
    void givenInvalidStatusWhenFetchAuthenticationDataThenError() {
        //given
        final FacebookAccessToken facebookAccessToken = new FacebookAccessToken("token");

        when(facebookConfiguration.getFetchAuthenticationDataWantedFields())
                .thenReturn("email,first_name,last_name,picture".split(","));
        when(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .thenReturn("https://graph.facebook.com/me");
        when(restTemplate.getForEntity(anyString(), eq(FacebookAuthenticationDataResponse.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.fetchAuthenticationData(facebookAccessToken));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenLackOfFirstNameWhenFetchAuthenticationDataThenError() {
        //given
        final FacebookAccessToken facebookAccessToken = new FacebookAccessToken("token");
        final FacebookAuthenticationDataResponse response =
                new FacebookAuthenticationDataResponse("email", null, "lastName", "pictureUrl");

        when(facebookConfiguration.getFetchAuthenticationDataWantedFields())
                .thenReturn("email,first_name,last_name,picture".split(","));
        when(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .thenReturn("https://graph.facebook.com/me");
        when(restTemplate.getForEntity(anyString(), eq(FacebookAuthenticationDataResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.fetchAuthenticationData(facebookAccessToken));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenLackOfLastNameWhenFetchAuthenticationDataThenError() {
        //given
        final FacebookAccessToken facebookAccessToken = new FacebookAccessToken("token");
        final FacebookAuthenticationDataResponse response =
                new FacebookAuthenticationDataResponse("email", "firstName", null, "pictureUrl");

        when(facebookConfiguration.getFetchAuthenticationDataWantedFields())
                .thenReturn("email,first_name,last_name,picture".split(","));
        when(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .thenReturn("https://graph.facebook.com/me");
        when(restTemplate.getForEntity(anyString(), eq(FacebookAuthenticationDataResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.fetchAuthenticationData(facebookAccessToken));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenLackOfEmailWhenFetchAuthenticationDataThenError() {
        //given
        final FacebookAccessToken facebookAccessToken = new FacebookAccessToken("token");
        final FacebookAuthenticationDataResponse response =
                new FacebookAuthenticationDataResponse(null, "firstName", "lastName", "pictureUrl");

        when(facebookConfiguration.getFetchAuthenticationDataWantedFields())
                .thenReturn("email,first_name,last_name,picture".split(","));
        when(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .thenReturn("https://graph.facebook.com/me");
        when(restTemplate.getForEntity(anyString(), eq(FacebookAuthenticationDataResponse.class)))
                .thenReturn(ResponseEntity.ok(response));

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.fetchAuthenticationData(facebookAccessToken));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }

    @Test
    void givenErrorWhenFetchAuthenticationDataThenError() {
        //given
        final FacebookAccessToken facebookAccessToken = new FacebookAccessToken("token");

        when(facebookConfiguration.getFetchAuthenticationDataWantedFields())
                .thenReturn("email,first_name,last_name,picture".split(","));
        when(facebookConfiguration.getFetchAuthenticationDataBaseUrl())
                .thenReturn("https://graph.facebook.com/me");
        when(restTemplate.getForEntity(anyString(), eq(FacebookAuthenticationDataResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.CREATED));

        //when
        final Throwable throwable = catchThrowable(() -> facebookExecutingService.fetchAuthenticationData(facebookAccessToken));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
    }
}