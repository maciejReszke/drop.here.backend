package com.drop.here.backend.drophere.notification.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "googlecredentials")
    public GoogleCredentialsConfiguration googleCredentialsConfiguration() {
        return new GoogleCredentialsConfiguration();
    }
}
