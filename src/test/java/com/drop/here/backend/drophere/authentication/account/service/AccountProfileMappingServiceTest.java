package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountProfileMappingServiceTest {

    @InjectMocks
    private AccountProfileMappingService accountProfileMappingService;

    @Test
    void givenProfileCreationRequestWhenNewAccountProfileThenMap() {
        //given
        final AccountProfileCreationRequest accountProfileCreationRequest = AccountProfileDataGenerator.accountProfileRequest(1);
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final String encodedPassword = "encoded";
        final AccountProfileType accountProfileType = AccountProfileType.MAIN;

        //when
        final AccountProfile result = accountProfileMappingService.newAccountProfile(accountProfileCreationRequest, encodedPassword, accountProfileType, account);

        //then
        assertThat(result.getProfileUid()).isNotEmpty();
        assertThat(result.getFirstName()).isEqualTo(accountProfileCreationRequest.getFirstName());
        assertThat(result.getLastName()).isEqualTo(accountProfileCreationRequest.getLastName());
        assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getDeactivatedAt()).isNull();
        assertThat(result.getStatus()).isEqualTo(AccountProfileStatus.ACTIVE);
        assertThat(result.getProfileType()).isEqualTo(accountProfileType);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getAccount()).isEqualTo(account);
        assertThat(result.getVersion()).isNull();
    }

}