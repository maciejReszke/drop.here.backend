package com.drop.here.backend.drophere.authentication.account.dto;

import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountInformationResponse {

    String mail;

    AccountType accountType;

    AccountStatus accountStatus;

    AccountMailStatus accountMailStatus;

    String createdAt;
}
