package com.drop.here.backend.drophere.authentication.account.dto;

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
    private String mail;

    @NotBlank
    @Length(min = 4, max = 255)
    private String password;

    @NotBlank
    private String accountType;
}
