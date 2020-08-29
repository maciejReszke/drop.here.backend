package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountProfileService {
    private final AccountProfilePersistenceService accountProfilePersistenceService;
    private final AccountProfileValidationService profileValidationService;
    private final PasswordEncoder passwordEncoder;
    private final AccountProfileMappingService accountProfileMappingService;
    private final AccountService accountService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final PrivilegeService privilegeService;

    public Optional<AccountProfile> findActiveByAccountAndProfileUidWithRoles(Account account, String profileUid) {
        return accountProfilePersistenceService.findByAccountAndProfileUidWithRoles(account, profileUid)
                .filter(profile -> profile.getStatus() == AccountProfileStatus.ACTIVE);
    }

    public boolean isPasswordValid(AccountProfile profile, String rawPassword) {
        return passwordEncoder.matches(rawPassword, profile.getPassword());
    }

    @Transactional
    public LoginResponse createAccountProfile(AccountProfileCreationRequest accountProfileRequest, AccountAuthentication accountAuthentication) {
        final Account account = accountAuthentication.getPrincipal();
        profileValidationService.validateRequest(accountProfileRequest, account);
        final String encodedPassword = encodePassword(accountProfileRequest);
        final AccountProfileType profileType = accountService.accountProfileCreated(account);
        final AccountProfile accountProfile = accountProfileMappingService.newAccountProfile(accountProfileRequest, encodedPassword, profileType, account);
        accountProfilePersistenceService.createProfile(accountProfile);
        privilegeService.addNewAccountProfilePrivileges(accountProfile);
        return authenticationExecutiveService.successLogin(account, accountProfile);
    }

    private String encodePassword(AccountProfileCreationRequest accountCreationRequest) {
        return passwordEncoder.encode(accountCreationRequest.getPassword().trim());
    }

    public void updateAccountProfile(AccountProfileUpdateRequest accountCreationRequest, AccountAuthentication accountAuthentication) {
        final AccountProfile profile = accountAuthentication.getProfile();
        profile.setFirstName(accountCreationRequest.getFirstName());
        profile.setLastName(accountCreationRequest.getLastName());
        accountProfilePersistenceService.updateProfile(profile);
    }
}
