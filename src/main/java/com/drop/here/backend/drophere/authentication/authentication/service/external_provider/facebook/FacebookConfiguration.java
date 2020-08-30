package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook;

import lombok.Data;

@Data
public class FacebookConfiguration {
    private String clientId;
    private String clientSecret;
    private String exchangeTokenBaseUrl;
    private String fetchAuthenticationDataBaseUrl;
    private String[] fetchAuthenticationDataWantedFields;
}
