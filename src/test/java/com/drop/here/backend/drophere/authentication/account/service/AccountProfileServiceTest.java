package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountProfileServiceTest {

    @InjectMocks
    private AccountProfileService accountProfileService;

    @Mock
    private AccountProfileRepository accountProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void givenExistingActiveProfileWhenFindActiveByAccountAndProfileUidThenGet() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final String profileUid = "profileUid";
        final AccountProfile profile = AccountProfileDataGenerator.accountProfile(1, account);
        profile.setStatus(AccountProfileStatus.ACTIVE);
        when(accountProfileRepository.findByAccountAndProfileUidWithRoles(account, profileUid)).thenReturn(Optional.of(profile));
        //when
        final Optional<AccountProfile> result = accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, profileUid);

        //then
        assertThat(result.orElseThrow()).isEqualTo(profile);
    }

    @Test
    void givenExistingNotActiveProfileWhenFindActiveByAccountAndProfileUidThenEmpty() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final String profileUid = "profileUid";
        final AccountProfile profile = AccountProfileDataGenerator.accountProfile(1, account);
        profile.setStatus(AccountProfileStatus.INACTIVE);
        when(accountProfileRepository.findByAccountAndProfileUidWithRoles(account, profileUid)).thenReturn(Optional.of(profile));
        //when
        final Optional<AccountProfile> result = accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, profileUid);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void givenMatchingPasswordWhenIsPasswordValidThenTrue() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final String password = "password";
        when(passwordEncoder.matches(password, accountProfile.getPassword())).thenReturn(true);

        //when
        final boolean result = accountProfileService.isPasswordValid(accountProfile, password);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotMatchingPasswordWhenIsPasswordValidThenFalse() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final String password = "password";
        when(passwordEncoder.matches(password, accountProfile.getPassword())).thenReturn(false);

        //when
        final boolean result = accountProfileService.isPasswordValid(accountProfile, password);

        //then
        assertThat(result).isFalse();
    }

}