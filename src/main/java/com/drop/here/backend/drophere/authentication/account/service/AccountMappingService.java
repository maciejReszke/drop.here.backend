package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInfoResponse;
import com.drop.here.backend.drophere.authentication.account.dto.ProfileInfoResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountRegistrationType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountMappingService {
    private final AccountProfilePersistenceService accountProfilePersistenceService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Account newAccount(AccountCreationRequest accountCreationRequest, String encodedPassword) {
        return Account.builder()
                .mail(accountCreationRequest.getMail().trim())
                .password(encodedPassword)
                .accountType(AccountType.parseIgnoreCase(accountCreationRequest.getAccountType()))
                .accountStatus(AccountStatus.ACTIVE)
                .accountMailStatus(AccountMailStatus.UNCONFIRMED)
                .createdAt(LocalDateTime.now())
                .isAnyProfileRegistered(false)
                .registrationType(AccountRegistrationType.FORM)
                .build();
    }

    public AccountInfoResponse toAccountInfoResponse(Account account) {
        final List<ProfileInfoResponse> profiles = accountProfilePersistenceService.findByAccount(account)
                .stream()
                .map(this::toProfileInfoResponse)
                .collect(Collectors.toList());

        return AccountInfoResponse.builder()
                .mail(account.getMail())
                .accountType(account.getAccountType())
                .accountStatus(account.getAccountStatus())
                .accountMailStatus(account.getAccountMailStatus())
                .createdAt(account.getCreatedAt().format(TIME_FORMATTER))
                .isAnyProfileRegistered(account.isAnyProfileRegistered())
                .profiles(profiles)
                .build();
    }

    private ProfileInfoResponse toProfileInfoResponse(AccountProfile profile) {
        return ProfileInfoResponse.builder()
                .profileUid(profile.getProfileUid())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .status(profile.getStatus())
                .profileType(profile.getProfileType())
                .build();
    }

    public Account newAccount(ExternalAuthenticationResult result) {
        return Account.builder()
                .mail(result.getEmail())
                .registrationType(AccountRegistrationType.EXTERNAL_PROVIDER)
                .accountType(AccountType.CUSTOMER)
                .accountStatus(AccountStatus.ACTIVE)
                .accountMailStatus(AccountMailStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .isAnyProfileRegistered(false)
                .build();
    }
}
