package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// TODO MONO:
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

    // todo bylo transactional(readOnly = true)
    public Image findImage(String profileUid) {
        return accountProfileRepository.findByProfileUidWithImage(profileUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Image for account profile %s was not found", profileUid),
                        RestExceptionStatusCode.ACCOUNT_PROFILE_IMAGE_WAS_NOT_FOUND))
                .getImage();
    }
}
