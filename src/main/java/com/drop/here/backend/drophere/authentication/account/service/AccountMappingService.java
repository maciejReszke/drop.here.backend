package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInformationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AccountMappingService {
    // TODO: 02/08/2020
    //private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public AccountInformationResponse toAccountInformationResponse(Account account) {
        return AccountInformationResponse.builder()
                .mail(account.getMail())
                .accountType(account.getAccountType())
                .accountMailStatus(account.getAccountMailStatus())
                .accountStatus(account.getAccountStatus())
                .createdAt(account.getCreatedAt().format(DATE_TIME_FORMATTER))
                .build();
    }

    public Account newAccount(AccountCreationRequest accountCreationRequest) {
        return Account.builder()
                .mail(accountCreationRequest.getMail().trim())
                .password(accountCreationRequest.getPassword().trim())
                .accountType(AccountType.parseIgnoreCase(accountCreationRequest.getAccountType()))
                .accountStatus(AccountStatus.ACTIVE)
                .accountMailStatus(AccountMailStatus.UNCONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
