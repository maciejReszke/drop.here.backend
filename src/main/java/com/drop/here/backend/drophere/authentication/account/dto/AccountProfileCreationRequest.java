package com.drop.here.backend.drophere.authentication.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountProfileCreationRequest {

    @NotBlank
    @Length(min = 2, max = 30)
    @ApiModelProperty(value = "User first name", example = "Miotr", required = true)
    private String firstName;

    @NotBlank
    @Length(min = 2, max = 30)
    @ApiModelProperty(value = "User last name", example = "Paszota", required = true)
    private String lastName;

    @NotBlank
    @Length(min = 4, max = 255)
    @ApiModelProperty(value = "Password", example = "abcd213123", required = true)
    private String password;
}
