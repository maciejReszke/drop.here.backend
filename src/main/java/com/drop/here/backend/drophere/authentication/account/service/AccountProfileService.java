package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
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
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

// TODO MONO:
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProfileService {
    private final AccountProfilePersistenceService accountProfilePersistenceService;
    private final AccountProfileValidationService profileValidationService;
    private final PasswordEncoder passwordEncoder;
    private final AccountProfileMappingService accountProfileMappingService;
    private final AccountService accountService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final PrivilegeService privilegeService;
    private final ImageService imageService;

    public Mono<AccountProfile> findActiveProfile(Account account, String profileUid) {
        return account.getProfiles().stream()
                .filter(accountProfile -> accountProfile.getProfileUid().equalsIgnoreCase(profileUid))
                .filter(accountProfile -> accountProfile.getStatus() == AccountProfileStatus.ACTIVE)
                .findFirst()
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    public boolean isPasswordValid(AccountProfile profile, String rawPassword) {
        return passwordEncoder.matches(rawPassword, profile.getPassword());
    }

    // todo bylo transactional
    public Mono<LoginResponse> createAccountProfile(AccountProfileCreationRequest accountProfileRequest, AccountAuthentication accountAuthentication) {
        final Account account = accountAuthentication.getPrincipal();
        profileValidationService.validateRequest(accountProfileRequest, account);
        final String encodedPassword = encodePassword(accountProfileRequest);
        final AccountProfileType profileType = accountService.addProfile(account, );
        final AccountProfile accountProfile = accountProfileMappingService.newAccountProfile(accountProfileRequest, encodedPassword, profileType, account);
        accountProfilePersistenceService.createProfile(accountProfile);
        privilegeService.addNewAccountProfilePrivileges(accountProfile);
        return authenticationExecutiveService.successLogin(account, accountProfile);
    }

    private String encodePassword(AccountProfileCreationRequest accountCreationRequest) {
        return passwordEncoder.encode(accountCreationRequest.getPassword().trim());
    }

    public Mono<Void> updateAccountProfile(AccountProfileUpdateRequest accountCreationRequest, AccountAuthentication accountAuthentication) {
        final AccountProfile profile = accountAuthentication.getProfile();
        profile.setFirstName(accountCreationRequest.getFirstName());
        profile.setLastName(accountCreationRequest.getLastName());
        accountProfilePersistenceService.updateProfile(profile);
    }

    public Mono<ResourceOperationResponse> updateImage(FilePart filePart, AccountAuthentication authentication) {
        try {
            final Image image = imageService.createImage(imagePart.getBytes(), ImageType.ACCOUNT_PROFILE_IMAGE);
            final AccountProfile accountProfile = authentication.getProfile();
            accountProfile.setImage(image);
            log.info("Updating image for account profile {}", accountProfile.getProfileUid());
            accountProfilePersistenceService.updateProfile(accountProfile);
            return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, accountProfile.getId());
        } catch (IOException exception) {
            throw new RestIllegalRequestValueException("Invalid image " + exception.getMessage(),
                    RestExceptionStatusCode.UPDATE_ACCOUNT_PROFILE_IMAGE_INVALID_IMAGE);
        }
    }

    public Mono<Image> findImage(String profileUid) {
        return accountProfilePersistenceService.findImage(profileUid);
    }
}
