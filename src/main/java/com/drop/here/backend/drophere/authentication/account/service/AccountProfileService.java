package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProfileService {
    private final AccountProfileValidationService profileValidationService;
    private final PasswordEncoder passwordEncoder;
    private final AccountProfileMappingService accountProfileMappingService;
    private final AccountService accountService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final PrivilegeService privilegeService;
    private final ImageService imageService;

    public Mono<AccountProfile> findActiveProfile(Account account, String profileUid) {
        return findProfile(account, profileUid)
                .filter(accountProfile -> accountProfile.getStatus() == AccountProfileStatus.ACTIVE)
                .findFirst()
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    private Stream<@Valid AccountProfile> findProfile(Account account, String profileUid) {
        return account.getProfiles().stream()
                .filter(accountProfile -> accountProfile.getProfileUid().equalsIgnoreCase(profileUid));
    }

    public boolean isPasswordValid(AccountProfile profile, String rawPassword) {
        return passwordEncoder.matches(rawPassword, profile.getPassword());
    }

    public Mono<LoginResponse> createAccountProfile(AccountProfileCreationRequest accountProfileRequest, AccountAuthentication accountAuthentication) {
        final Account account = accountAuthentication.getPrincipal();
        profileValidationService.validateRequest(accountProfileRequest, account);
        final String encodedPassword = encodePassword(accountProfileRequest);
        final AccountProfileType profileType = accountService.getProfileType(account);
        final AccountProfile accountProfile = accountProfileMappingService.newAccountProfile(accountProfileRequest, encodedPassword, profileType, account);
        log.info("Saving account profile for profile {} with uid {}", account.getId(), accountProfile.getProfileUid());
        privilegeService.addNewAccountProfilePrivileges(accountProfile);
        return accountService.addProfile(account, accountProfile)
                .map(profile -> authenticationExecutiveService.successLogin(account, profile));
    }

    private String encodePassword(AccountProfileCreationRequest accountCreationRequest) {
        return passwordEncoder.encode(accountCreationRequest.getPassword().trim());
    }

    public Mono<Void> updateAccountProfile(AccountProfileUpdateRequest accountCreationRequest, AccountAuthentication accountAuthentication) {
        final AccountProfile profile = accountAuthentication.getProfile();
        profile.setFirstName(accountCreationRequest.getFirstName());
        profile.setLastName(accountCreationRequest.getLastName());
        log.info("Updating account profile for profile {} with uid {}", accountAuthentication.getPrincipal().getId(), profile.getProfileUid());
        return accountService.updateProfile(accountAuthentication.getPrincipal(), profile);
    }

    public Mono<ResourceOperationResponse> updateImage(FilePart filePart, AccountAuthentication authentication) {
        final AccountProfile accountProfile = authentication.getProfile();
        log.info("Updating image for account profile {}", accountProfile.getProfileUid());
        return imageService.updateImage(filePart, ImageType.ACCOUNT_PROFILE_IMAGE, accountProfile.getProfileUid())
                .map(image -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, accountProfile.getProfileUid()));
    }

    public Mono<Image> findImage(String profileUid) {
        return accountService.findByProfileUid(profileUid)
                .flatMap(account -> findActiveProfile(account, profileUid))
                .flatMap(accountProfile -> imageService.findImage(accountProfile.getProfileUid(), ImageType.ACCOUNT_PROFILE_IMAGE));
    }
}
