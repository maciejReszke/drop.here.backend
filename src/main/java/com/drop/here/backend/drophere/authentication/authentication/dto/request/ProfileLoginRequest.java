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
public class ProfileLoginRequest {

    @NotBlank
    @ApiModelProperty(value = "User profile id", example = "profileId123", required = true)
    private String profileUid;

    @NotBlank
    @ApiModelProperty(value = "User profile password", example = "asdasiubd213", required = true)
    private String password;

}
