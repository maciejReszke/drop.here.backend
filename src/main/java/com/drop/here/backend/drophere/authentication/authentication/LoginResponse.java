package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String token;

    private String tokenValidUntil;

    private AccountType accountType;

    // TODO: 03/08/2020 role
}
