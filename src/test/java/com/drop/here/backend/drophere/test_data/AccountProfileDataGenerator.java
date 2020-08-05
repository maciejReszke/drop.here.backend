package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
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
                .profileUid("uid")
                .build();
    }
}
