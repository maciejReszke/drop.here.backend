package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInformationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountMappingServiceTest {

    @InjectMocks
    private AccountMappingService accountMappingService;

    @Test
    void givenUserWhenToAccountInformationResponseThenMap() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);

        //when
        final AccountInformationResponse response = accountMappingService.toAccountInformationResponse(account);

        //then
        assertThat(response.getAccountMailStatus()).isEqualTo(account.getAccountMailStatus());
        assertThat(response.getAccountStatus()).isEqualTo(account.getAccountStatus());
        assertThat(response.getMail()).isEqualTo(account.getMail());
        assertThat(response.getCreatedAt()).isEqualTo(account.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(response.getAccountType()).isEqualTo(account.getAccountType());
    }

    @Test
    void givenRequestWhenNewAccountThenMap() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);

        //when
        final Account response = accountMappingService.newAccount(request);

        //then
        assertThat(response.getAccountType()).isEqualTo(AccountType.parseIgnoreCase(request.getAccountType()));
        assertThat(response.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(response.getMail()).isEqualTo(request.getMail());
        assertThat(response.getPassword()).isEqualTo(request.getPassword());
        assertThat(response.getMailActivatedAt()).isNull();
        assertThat(response.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.getAccountMailStatus()).isEqualTo(AccountMailStatus.UNCONFIRMED);
    }

}