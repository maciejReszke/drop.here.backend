package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class AccountProfileDataGenerator {

    public AccountProfile accountProfile(int i, Account account) {
        return AccountProfile.builder()
                .createdAt(LocalDateTime.now())
                .firstName("firstName" + i)
                .lastName("lastName" + i)
                .password("password" + i)
                .account(account)
                .status(AccountProfileStatus.ACTIVE)
                .profileType(AccountProfileType.MAIN)
                .profileUid("uid" + i)
                .build();
    }

    public AccountProfileCreationRequest accountProfileRequest(int i) {
        return AccountProfileCreationRequest.builder()
                .firstName("firstName" + i)
                .lastName("lastName" + i)
                .password("password" + i)
                .build();
    }

    public AccountProfileUpdateRequest accountProfileUpdateRequest(int i) {
        return AccountProfileUpdateRequest.builder()
                .firstName("newFirstName" + i)
                .lastName("newLastName" + i)
                .build();
    }
}
