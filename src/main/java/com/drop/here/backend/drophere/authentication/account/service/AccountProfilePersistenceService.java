package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Optional<AccountProfile> findByDrop(Drop drop) {
        return accountProfileRepository.findByDrop(drop);
    }

    @Transactional(readOnly = true)
    public Image findImage(String profileUid) {
        return accountProfileRepository.findByProfileUidWithImage(profileUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Image for account profile %s was not found", profileUid),
                        RestExceptionStatusCode.ACCOUNT_PROFILE_IMAGE_WAS_NOT_FOUND))
                .getImage();
    }

    public Optional<AccountProfile> findActiveByCompanyAndProfileUid(Company company, String profileUid) {
        return accountProfileRepository.findByAccountCompanyAndProfileUidAndStatus(company, profileUid, AccountProfileStatus.ACTIVE);
    }

    public AccountProfile findById(Long id) {
        return accountProfileRepository.findById(id)
                .orElseThrow(() -> new RestEntityNotFoundException(
                        String.format("Account profile by id %s not found ", id),
                        RestExceptionStatusCode.ACCOUNT_PROFILE_BY_ID_NOT_FOUND));
    }

    public boolean existsByAccountAndProfileUid(Account account, String profileUid) {
        return accountProfileRepository.existsByAccountAndProfileUid(account, profileUid);
    }

    public Long count(Account account) {
        return accountProfileRepository.countByAccount(account);
    }
}
