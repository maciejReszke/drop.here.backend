package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountProfileMappingService {

    public AccountProfile newAccountProfile(AccountProfileCreationRequest accountProfileRequest, String encodedPassword, AccountProfileType profileType, Account account) {
        return AccountProfile.builder()
                .status(AccountProfileStatus.ACTIVE)
                .privileges(List.of())
                .password(encodedPassword)
                .profileUid(account.getId() + UUID.randomUUID().toString())
                .profileType(profileType)
                .createdAt(LocalDateTime.now())
                .firstName(accountProfileRequest.getFirstName().trim())
                .lastName(accountProfileRequest.getLastName().trim())
                .build();
    }
}
