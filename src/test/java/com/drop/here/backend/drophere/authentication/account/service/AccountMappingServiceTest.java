package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInfoResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountMailStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountRegistrationType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountMappingServiceTest {

    @InjectMocks
    private AccountMappingService accountMappingService;

    @Test
    void givenRequestWhenNewAccountThenMap() {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);

        //when
        final Account response = accountMappingService.newAccount(request, "encodedPassword");

        //then
        assertThat(response.getAccountType()).isEqualTo(AccountType.parseIgnoreCase(request.getAccountType()));
        assertThat(response.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(response.getMail()).isEqualTo(request.getMail());
        assertThat(response.getPassword()).isEqualTo("encodedPassword");
        assertThat(response.getMailActivatedAt()).isNull();
        assertThat(response.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.getAccountMailStatus()).isEqualTo(AccountMailStatus.UNCONFIRMED);
        assertThat(response.isAnyProfileRegistered()).isFalse();
        assertThat(response.getRegistrationType()).isEqualTo(AccountRegistrationType.FORM);
        assertThat(response.getPrivileges()).isNotNull();
        assertThat(response.getPrivileges()).isEmpty();
    }

    @Test
    void givenAccountWhenToAccountInfoResponseThenMap() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile profile = AccountProfileDataGenerator.accountProfile(1, account);
        account.setProfiles(List.of(profile));

        //when
        final Mono<AccountInfoResponse> response = accountMappingService.toAccountInfoResponse(account);

        //then
        StepVerifier.create(response)
                .assertNext(result -> {
                    assertThat(result.getAccountMailStatus()).isEqualTo(account.getAccountMailStatus());
                    assertThat(result.getCreatedAt()).isEqualTo(account.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    assertThat(result.getAccountStatus()).isEqualTo(account.getAccountStatus());
                    assertThat(result.getMail()).isEqualTo(account.getMail());
                    assertThat(result.getAccountType()).isEqualTo(account.getAccountType());
                    assertThat(result.getProfiles()).hasSize(1);
                    assertThat(result.getProfiles().get(0).getFirstName()).isEqualTo(profile.getFirstName());
                    assertThat(result.getProfiles().get(0).getLastName()).isEqualTo(profile.getLastName());
                    assertThat(result.getProfiles().get(0).getProfileUid()).isEqualTo(profile.getProfileUid());
                    assertThat(result.getProfiles().get(0).getProfileType()).isEqualTo(profile.getProfileType());
                    assertThat(result.getProfiles().get(0).getStatus()).isEqualTo(profile.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void givenExternalAuthenticationResultWhenNewAccountThenCreate() {
        //given
        final ExternalAuthenticationResult authenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);

        //when
        final Account response = accountMappingService.newAccount(authenticationResult);

        //then
        assertThat(response.getAccountType()).isEqualTo(AccountType.CUSTOMER);
        assertThat(response.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(response.getMail()).isEqualTo(authenticationResult.getEmail());
        assertThat(response.getPassword()).isNull();
        assertThat(response.getMailActivatedAt()).isNull();
        assertThat(response.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.getAccountMailStatus()).isEqualTo(AccountMailStatus.CONFIRMED);
        assertThat(response.isAnyProfileRegistered()).isFalse();
        assertThat(response.getRegistrationType()).isEqualTo(AccountRegistrationType.EXTERNAL_PROVIDER);
    }
}