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
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountProfileServiceTest {

    @InjectMocks
    private AccountProfileService accountProfileService;

    @Mock
    private AccountProfilePersistenceService accountProfilePersistenceService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountProfileValidationService accountProfileValidationService;

    @Mock
    private PrivilegeService privilegeService;

    @Mock
    private AccountProfileMappingService accountProfileMappingService;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthenticationExecutiveService authenticationExecutiveService;

    @Mock
    private ImageService imageService;

    @Test
    void givenExistingActiveProfileWhenFindActiveByAccountAndProfileUidThenGet() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final String profileUid = "profileUid";
        final AccountProfile profile = AccountProfileDataGenerator.accountProfile(1, account);
        profile.setStatus(AccountProfileStatus.ACTIVE);
        when(accountProfilePersistenceService.findByAccountAndProfileUidWithRoles(account, profileUid)).thenReturn(Optional.of(profile));
        //when
        final Optional<AccountProfile> result = accountProfileService.findActiveProfile(account, profileUid);

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
        when(accountProfilePersistenceService.findByAccountAndProfileUidWithRoles(account, profileUid)).thenReturn(Optional.of(profile));
        //when
        final Optional<AccountProfile> result = accountProfileService.findActiveProfile(account, profileUid);

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

    @Test
    void givenAccountProfileRequestWhenCreateAccountProfileThenSave() {
        //given
        final AccountProfileCreationRequest request = AccountProfileDataGenerator.accountProfileRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final LoginResponse loginResponse = new LoginResponse();

        doNothing().when(accountProfileValidationService).validateRequest(request, account);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(accountProfileMappingService.newAccountProfile(request, "encodedPassword", AccountProfileType.MAIN, account)).thenReturn(accountProfile);
        doNothing().when(privilegeService).addNewAccountProfilePrivileges(accountProfile);
        when(accountService.addProfile(account)).thenReturn(AccountProfileType.MAIN);
        when(authenticationExecutiveService.successLogin(account, accountProfile)).thenReturn(loginResponse);
        doNothing().when(accountProfilePersistenceService).createProfile(accountProfile);

        //when
        final LoginResponse response = accountProfileService.createAccountProfile(request, accountAuthentication);

        //then
        assertThat(response).isEqualTo(loginResponse);
    }

    @Test
    void givenAccountProfileUpdateRequestWhenUpdateAccountProfileThenUpdate() {
        //given
        final AccountProfileUpdateRequest accountProfileRequest = AccountProfileDataGenerator.accountProfileUpdateRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);

        doNothing().when(accountProfilePersistenceService).updateProfile(accountProfile);

        //when
        accountProfileService.updateAccountProfile(accountProfileRequest, accountAuthentication);

        //then
        assertThat(accountProfile.getFirstName()).isEqualTo(accountProfileRequest.getFirstName());
        assertThat(accountProfile.getLastName()).isEqualTo(accountProfileRequest.getLastName());
    }

    @Test
    void givenImageWhenUpdateImageThenUpdate() throws IOException {
        //given
        final MockMultipartFile image = new MockMultipartFile("name", "byte".getBytes());
        final Account account = Account.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .profile(accountProfile)
                .build();

        final Image imageEntity = Image.builder().build();
        when(imageService.createImage(image.getBytes(), ImageType.ACCOUNT_PROFILE_IMAGE))
                .thenReturn(imageEntity);
        doNothing().when(accountProfilePersistenceService).updateProfile(accountProfile);

        //when
        final ResourceOperationResponse resourceOperationResponse = accountProfileService.updateImage(image, accountAuthentication);

        //then
        assertThat(resourceOperationResponse.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(accountProfile.getImage()).isEqualTo(imageEntity);
    }


}