package com.drop.here.backend.drophere.notification.configuration;

import lombok.Data;

@Data
public class GoogleCredentialsConfiguration {
    private String type;

    private String projectId;

    private String privateKeyId;

    private String privateKey;

    private String clientEmail;

    private String clientId;

    private String authUri;

    private String tokenUri;

    private String authProviderX509CertUrl;

    private String clientX509CertUrl;
}
