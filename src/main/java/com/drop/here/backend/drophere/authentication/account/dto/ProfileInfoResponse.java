package com.drop.here.backend.drophere.authentication.account.dto;

import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileInfoResponse {

    @ApiModelProperty(value = "Profile uid", example = "adsin[012i3")
    private String profileUid;

    @ApiModelProperty(value = "First name", example = "Miotr")
    private String firstName;

    @ApiModelProperty(value = "Last name", example = "Kamszota")
    private String lastName;

    @ApiModelProperty(value = "Account profile status", example = "ACTIVE")
    private AccountProfileStatus status;

    @ApiModelProperty(value = "Account profile type", example = "MAIN")
    private AccountProfileType profileType;
}
