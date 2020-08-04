package com.drop.here.backend.drophere.authentication.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreationRequest {

    @NotBlank
    @Email
    @Length(max = 320)
    @ApiModelProperty(value = "Valid mail", example = "maszkamszota@gmail.com", required = true)
    private String mail;

    @NotBlank
    @Length(min = 4, max = 255)
    @ApiModelProperty(value = "Password,", example = "abcd213123",required = true)
    private String password;

    @NotBlank
    @ApiModelProperty(value = "Account type - CUSTOMER or COMPANY", example = "COMPANY", required = true)
    private String accountType;
}
