package com.drop.here.backend.drophere.authentication.authentication;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    @ApiModelProperty(value = "Authentication token", example = "acxzhu9sndao")
    private String token;

    @ApiModelProperty(value = "Date which indicates how long token is valid", example = "2020-04-06T11:03:32")
    private String tokenValidUntil;
}
