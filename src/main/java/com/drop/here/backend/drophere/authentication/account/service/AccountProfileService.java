package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountProfileService {
    private final AccountProfileRepository accountProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<AccountProfile> findActiveByAccountAndProfileUidWithRoles(Account account, String profileUid) {
        return accountProfileRepository.findByAccountAndProfileUidWithRoles(account, profileUid)
                .filter(profile -> profile.getStatus() == AccountProfileStatus.ACTIVE);
    }

    public boolean isPasswordValid(AccountProfile profile, String rawPassword) {
        return passwordEncoder.matches(rawPassword, profile.getPassword());
    }
}
