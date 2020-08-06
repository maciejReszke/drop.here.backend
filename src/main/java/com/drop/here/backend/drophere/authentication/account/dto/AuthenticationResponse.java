package com.drop.here.backend.drophere.authentication.account.dto;

import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class AuthenticationResponse {

    @ApiModelProperty(value = "Account type", example = "COMPANY")
    private AccountType accountType;

    @ApiModelProperty(value = "List of roles", example = "SEXTING, EDITING_COMPANY_INFO")
    private List<String> roles;

    @ApiModelProperty(value = "Date which indicates how long token is valid", example = "2020-04-06T11:03:32")
    private String tokenValidUntil;

    @ApiModelProperty(value = "User mail", example = "kam@szota@gmail.com")
    private String mail;

    @ApiModelProperty(value = "Account status", example = "ACTIVE")
    private AccountStatus accountStatus;

    @ApiModelProperty(value = "Indicates if user has any profile", example = "true")
    private boolean hasProfile;

    @ApiModelProperty(value = "Indicates if user is currently logged on any profile", example = "true")
    private boolean loggedOnProfile;

    @ApiModelProperty(value = "Profile uid", example = "uid1234")
    private String profileUid;

    @ApiModelProperty(value = "First name + last name from profile", example = "Miotr Paszota")
    private String profileName;

    @ApiModelProperty(value = "Profile type - MAIN or SUBPROFILE", example = "MAIN")
    private AccountProfileType profileType;
}
