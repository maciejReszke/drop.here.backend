package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountRegistrationType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class AccountDataGenerator {

    public AccountCreationRequest accountCreationRequest(int i) {
        return AccountCreationRequest.builder()
                .accountType(AccountType.COMPANY.name())
                .mail("mail" + i + "@mail.pl")
                .password("password123#$" + i)
                .build();
    }

    public Account companyAccount(int i) {
        return Account.builder()
                .accountMailStatus(AccountMailStatus.CONFIRMED)
                .accountStatus(AccountStatus.ACTIVE)
                .accountType(AccountType.COMPANY)
                .createdAt(LocalDateTime.now())
                .isAnyProfileRegistered(false)
                .mail("mailAtCompany" + i + "@pl.pl")
                .mailActivatedAt(LocalDateTime.now())
                .password("password1234#" + i)
                .registrationType(AccountRegistrationType.FORM)
                .build();
    }

    public Account customerAccount(int i) {
        return Account.builder()
                .accountMailStatus(AccountMailStatus.CONFIRMED)
                .accountStatus(AccountStatus.ACTIVE)
                .accountType(AccountType.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .isAnyProfileRegistered(false)
                .mail("mailAtCompany" + i + "@pl.pl")
                .mailActivatedAt(LocalDateTime.now())
                .password("password1234#" + i)
                .registrationType(AccountRegistrationType.FORM)
                .build();
    }
}
