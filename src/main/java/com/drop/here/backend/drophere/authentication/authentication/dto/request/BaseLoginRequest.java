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
public class BaseLoginRequest {

    @NotBlank
    @ApiModelProperty(value = "User mail", example = "maszkamszota@gmail.com", required = true)
    private String mail;

    @NotBlank
    @ApiModelProperty(value = "User password", example = "asdasiubd213", required = true)
    private String password;
}
