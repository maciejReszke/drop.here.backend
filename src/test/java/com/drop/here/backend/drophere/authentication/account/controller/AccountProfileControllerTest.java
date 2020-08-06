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
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @AfterEach
    void cleanUp() {
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void givenValidRequestAndCorrectAccountWithPrivilegesWhenCreateAccountProfileThenCreate() throws Exception {
        //given

        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));

        final TokenResponse token = jwtService.createToken(account);
        final String url = String.format("/accounts/%s/profiles", account.getId());
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
        assertThat(privilegeRepository.findAll()).hasSize(2);
        assertThat(accountRepository.findAll().get(0).isAnyProfileRegistered()).isTrue();
    }

    @Test
    void givenValidRequestAndIncorrectAccountWithPrivilegesWhenCreateAccountProfileThenForbidden() throws Exception {
        //given

        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));

        final TokenResponse token = jwtService.createToken(account);
        final String url = String.format("/accounts/%s/profiles", account.getId() + 4);
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
    void givenValidRequestAndCorrectAccountWithoutPrivilegesWhenCreateAccountProfileThenForbidden() throws Exception {
        //given

        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE + "Aaa").build());
        account.setPrivileges(List.of(privilege));

        final TokenResponse token = jwtService.createToken(account);
        final String url = String.format("/accounts/%s/profiles", account.getId());
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
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());

        final TokenResponse token = jwtService.createToken(account, profile);
        final String url = String.format("/accounts/%s/profiles/%s", account.getId(), profile.getProfileUid());
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
    void givenValidRequestAndIncorrectAccountAndProfileWithPrivilegesWhenUpdateAccountProfileThenForbidden() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());

        final TokenResponse token = jwtService.createToken(account, profile);
        final String url = String.format("/accounts/%s/profiles/%s", account.getId() + 1, profile.getProfileUid());
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
    void givenValidRequestAndCorrectAccountAndIncorrectProfileWithPrivilegesWhenUpdateAccountProfileThenForbidden() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        account.setAccountType(AccountType.COMPANY);
        account.setAnyProfileRegistered(false);
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE).build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().accountProfile(profile).name("aa").build());

        final TokenResponse token = jwtService.createToken(account, profile);
        final String url = String.format("/accounts/%s/profiles/%s", account.getId(), profile.getProfileUid() + "aaa");
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
        final String url = String.format("/accounts/%s/profiles/%s", account.getId(), profile.getProfileUid());
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
}