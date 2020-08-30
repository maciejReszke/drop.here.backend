package com.drop.here.backend.drophere.authentication.account.dto;

import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountInfoResponse {

    @ApiModelProperty(value = "Account mail", example = "mail@mail.pl")
    private String mail;

    @ApiModelProperty(value = "Account type", example = "COMPANY")
    private AccountType accountType;

    @ApiModelProperty(value = "Account status", example = "ACTIVE")
    private AccountStatus accountStatus;

    @ApiModelProperty(value = "Account mail status", example = "COMPANY")
    private AccountMailStatus accountMailStatus;

    @ApiModelProperty(value = "Created at", example = "2020-12-31")
    private String createdAt;

    @ApiModelProperty(value = "Indicates if has any profile registered", example = "true")
    private boolean isAnyProfileRegistered;

    @ApiModelProperty(value = "Connected profiles")
    private List<ProfileInfoResponse> profiles;
}
