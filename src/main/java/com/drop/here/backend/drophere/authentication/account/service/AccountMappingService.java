package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountMappingService {

    public Account newAccount(AccountCreationRequest accountCreationRequest, String encodedPassword) {
        return Account.builder()
                .mail(accountCreationRequest.getMail().trim())
                .password(encodedPassword)
                .accountType(AccountType.parseIgnoreCase(accountCreationRequest.getAccountType()))
                .accountStatus(AccountStatus.ACTIVE)
                .accountMailStatus(AccountMailStatus.UNCONFIRMED)
                .createdAt(LocalDateTime.now())
                .isAnyProfileRegistered(false)
                .build();
    }
}
