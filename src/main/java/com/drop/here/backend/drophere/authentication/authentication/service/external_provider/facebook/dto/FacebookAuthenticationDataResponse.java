package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookAuthenticationDataResponse {

    private String email;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    private String pictureUrl;

    @JsonProperty(value = "picture")
    private void unpackPictureUrl(Map<String, Object> picture) {
        this.pictureUrl = Optional.ofNullable(picture)
                .flatMap(pic -> Optional.ofNullable((Map<String, Object>) picture.get("data")))
                .flatMap(pic -> Optional.ofNullable((String) pic.get("url")))
                .orElse(null);
    }
}
