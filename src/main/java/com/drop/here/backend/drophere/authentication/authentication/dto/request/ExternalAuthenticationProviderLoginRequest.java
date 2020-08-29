package com.drop.here.backend.drophere.authentication.authentication.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalAuthenticationProviderLoginRequest {

    @NotBlank
    @ApiModelProperty(value = "Redirect uri that was used in oauth", example = "http://localhost:8090/endpoint", required = true)
    private String redirectUri;

    @ApiModelProperty(value = "Received code", example = "XZC1adsacbou12epu(Gc9asc7c1", required = true)
    @NotBlank
    private String code;

    @ApiModelProperty(value = "Provider name", example = "FACEBOOK", required = true)
    @NotBlank
    private String provider;
}
