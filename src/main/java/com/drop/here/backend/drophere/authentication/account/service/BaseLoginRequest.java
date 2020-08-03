package com.drop.here.backend.drophere.authentication.account.service;

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
    private String mail;

    @NotBlank
    private String password;
}
