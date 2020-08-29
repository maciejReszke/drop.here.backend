package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountProfilePersistenceServiceTest {

    @InjectMocks
    private AccountProfilePersistenceService accountProfilePersistenceService;

    @Mock
    private AccountProfileRepository accountProfileRepository;

    @Test
    void whenFindByAccountAndProfileUidWithRolesThenFind() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final String profileUid = "profileUid";
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);

        when(accountProfileRepository.findByAccountAndProfileUidWithRoles(account, profileUid))
                .thenReturn(Optional.of(accountProfile));

        //when
        final Optional<AccountProfile> profile = accountProfilePersistenceService.findByAccountAndProfileUidWithRoles(account, profileUid);

        //then
        assertThat(profile).isPresent();
        assertThat(profile.orElseThrow()).isEqualTo(accountProfile);
    }

    @Test
    void whenFindByAccountThenFind() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);

        when(accountProfileRepository.findByAccount(account)).thenReturn(List.of(accountProfile));

        //when
        final List<AccountProfile> profiles = accountProfilePersistenceService.findByAccount(account);

        //then
        assertThat(profiles).hasSize(1);
        assertThat(profiles.get(0)).isEqualTo(accountProfile);
    }

    @Test
    void givenAccountProfileWhenCreateThenSave() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);

        when(accountProfileRepository.save(accountProfile)).thenReturn(accountProfile);

        //when
        accountProfilePersistenceService.createProfile(accountProfile);

        //then
        verifyNoMoreInteractions(accountProfileRepository);
    }

    @Test
    void givenAccountProfileWhenUpdateThenSave() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);

        when(accountProfileRepository.save(accountProfile)).thenReturn(accountProfile);

        //when
        accountProfilePersistenceService.updateProfile(accountProfile);

        //then
        verifyNoMoreInteractions(accountProfileRepository);
    }

}