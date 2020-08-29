package com.drop.here.backend.drophere.authentication.authentication.service.external_provider.facebook.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookAccessTokenResponse {

    @JsonProperty(value = "access_token")
    private String accessToken;
}
