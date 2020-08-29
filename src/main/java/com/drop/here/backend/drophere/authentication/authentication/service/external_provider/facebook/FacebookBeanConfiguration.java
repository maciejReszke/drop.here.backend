package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacebookBeanConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "externalauth.facebook")
    public FacebookConfiguration facebookConfiguration() {
        return new FacebookConfiguration();
    }
}
