package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProfilePersistenceService {
    private final AccountProfileRepository accountProfileRepository;

    public Optional<AccountProfile> findByAccountAndProfileUidWithRoles(Account account, String profileUid) {
        return accountProfileRepository.findByAccountAndProfileUidWithRoles(account, profileUid);
    }

    public void createProfile(AccountProfile accountProfile) {
        log.info("Saving account profile for profile {} with uid {}", accountProfile.getAccount().getId(), accountProfile.getProfileUid());
        accountProfileRepository.save(accountProfile);
        log.info("Successfully saved account profile for profile {} with uid {}", accountProfile.getAccount().getId(), accountProfile.getProfileUid());
    }

    public void updateProfile(AccountProfile profile) {
        log.info("Updating account profile for profile {} with uid {}", profile.getAccount().getId(), profile.getProfileUid());
        accountProfileRepository.save(profile);
        log.info("Successfully updated account profile for profile {} with uid {}", profile.getAccount().getId(), profile.getProfileUid());
    }

    public List<AccountProfile> findByAccount(Account account) {
        return accountProfileRepository.findByAccount(account);
    }
}
