package com.drop.here.backend.drophere.authentication.account.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountProfileControllerTest extends IntegrationBaseClass {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ImageRepository imageRepository;

    @AfterEach
    void cleanUp() {
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void givenEmptyProfilesValidRequestAndCorrectAccountWithPrivilegesWhenCreateAccountProfileThenCreate() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));

        final TokenResponse token = jwtService.createToken(account);
        final String url = "/accounts/profiles";
        final AccountProfileCreationRequest accountProfileCreationRequest = AccountProfileDataGenerator.accountProfileRequest(1);

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountProfileCreationRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyOrNullString())));

        assertThat(accountProfileRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(3);
        assertThat(privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase("COMPANY_FULL_MANAGEMENT"))).isNotEmpty();
        assertThat(accountRepository.findAll().get(0).isAnyProfileRegistered()).isTrue();
    }

    @Test
    void givenSecondProfileValidRequestAndCorrectAccountWithPrivilegesWhenCreateAccountProfileThenCreate() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(true);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account)
                .name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        accountRepository.save(account);
        accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));

        final TokenResponse token = jwtService.createToken(account);
        final String url = "/accounts/profiles";
        final AccountProfileCreationRequest accountProfileCreationRequest = AccountProfileDataGenerator.accountProfileRequest(1);

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountProfileCreationRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyOrNullString())));

        assertThat(accountProfileRepository.findAll()).hasSize(2);
        assertThat(privilegeRepository.findAll()).hasSize(2);
        assertThat(privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase("COMPANY_FULL_MANAGEMENT"))).isEmpty();
        assertThat(accountRepository.findAll().get(0).isAnyProfileRegistered()).isTrue();
    }

    @Test
    void givenValidRequestAndCorrectAccountWithoutPrivilegesWhenCreateAccountProfileThenForbidden() throws Exception {
        //given

        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE + "Aaa").build());
        account.setPrivileges(List.of(privilege));

        final TokenResponse token = jwtService.createToken(account);
        final String url = "/accounts/profiles";
        final AccountProfileCreationRequest accountProfileCreationRequest = AccountProfileDataGenerator.accountProfileRequest(1);

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountProfileCreationRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        perform.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestAndCorrectAccountAndProfileWithPrivilegesWhenUpdateAccountProfileThenNoContent() throws Exception {
        //given

        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());

        final TokenResponse token = jwtService.createToken(account, profile);
        final String url = "/accounts/profiles";
        final AccountProfileUpdateRequest accountProfileUpdateRequest = AccountProfileDataGenerator.accountProfileUpdateRequest(1);

        //when
        final ResultActions perform = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountProfileUpdateRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        perform.andExpect(status().isNoContent());

        assertThat(accountProfileRepository.findAll().get(0).getLastName()).isEqualTo(accountProfileUpdateRequest.getLastName());
    }

    @Test
    void givenValidRequestAndCorrectAccountAndProfileWithInvalidPrivilegesWhenUpdateAccountProfileThenForbidden() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE + "aaa").build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());

        final TokenResponse token = jwtService.createToken(account, profile);
        final String url = "/accounts/profiles";
        final AccountProfileUpdateRequest accountProfileUpdateRequest = AccountProfileDataGenerator.accountProfileUpdateRequest(1);

        //when
        final ResultActions perform = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountProfileUpdateRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        perform.andExpect(status().isForbidden());

        assertThat(accountProfileRepository.findAll().get(0).getLastName()).isNotEqualTo(accountProfileUpdateRequest.getLastName());
    }

    @Test
    void givenExistingAccountProfileImageWhenGetAccountProfileImageThenGet() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Image image = imageRepository.save(Image.builder().type(ImageType.CUSTOMER_IMAGE).bytes("bytes".getBytes()).build());
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = AccountProfileDataGenerator.accountProfile(1, account);
        profile.setImage(image);
        accountProfileRepository.save(profile);
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());
        final String url = String.format("/accounts/profiles/%s/images", profile.getProfileUid());


        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, profile).getToken()));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    void givenNotExistingAccountProfileImageWhenGetAccountProfileImageThen404() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());

        final String url = String.format("/accounts/profiles/%s/images", profile.getProfileUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, profile).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequestNotExistingImageWhenUpdateImageThenUpdate() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());

        final String url = "/accounts/profiles/images";
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        //when
        final ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, profile).getToken()));

        //then
        perform.andExpect(status().isOk());
        assertThat(imageRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestExistingImageWhenUpdateImageThenUpdate() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = AccountProfileDataGenerator.accountProfile(1, account);
        final Image image = imageRepository.save(Image.builder().bytes("aa".getBytes()).type(ImageType.CUSTOMER_IMAGE).build());
        profile.setImage(image);
        accountProfileRepository.save(profile);
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY).build());
        final String url = "/accounts/profiles/images";
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        //when
        final ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, profile).getToken()));

        //then
        perform.andExpect(status().isOk());
        assertThat(imageRepository.findAll()).hasSize(1);
        assertThat(imageRepository.findById(image.getId())).isEmpty();
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenUpdateImageThen403() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());
        final String url = "/accounts/profiles/images";
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        //when
        final ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, profile).getToken()));

        //then
        perform.andExpect(status().isForbidden());
        assertThat(imageRepository.findAll()).isEmpty();
    }
}