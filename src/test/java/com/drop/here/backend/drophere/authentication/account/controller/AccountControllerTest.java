package com.drop.here.backend.drophere.authentication.account.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends IntegrationBaseClass {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void cleanUp() {
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void givenValidRequestWhenCreateCompanyAccountThenCreate() throws Exception {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        request.setAccountType(AccountType.COMPANY.name());
        final String json = objectMapper.writeValueAsString(request);

        final String url = "/accounts";

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.tokenValidUntil", Matchers.not(Matchers.emptyString())));

        assertThat(accountRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll().get(0).getName()).isEqualTo("OWN_PROFILE_MANAGEMENT");
    }

    @Test
    void givenValidRequestWhenCreateCustomerAccountThenCreate() throws Exception {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        request.setAccountType(AccountType.CUSTOMER.name());
        final String json = objectMapper.writeValueAsString(request);

        final String url = "/accounts";

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.tokenValidUntil", Matchers.not(Matchers.emptyString())));

        assertThat(accountRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll().get(0).getName()).isEqualTo("CREATE_CUSTOMER");
    }

    @Test
    void givenExistingAccountRequestWhenCreateAccountThenError() throws Exception {
        //given
        final Account savedAccount = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        request.setMail(savedAccount.getMail());
        final String json = objectMapper.writeValueAsString(request);

        final String url = "/accounts";

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        result.andExpect(status().isUnprocessableEntity());

        assertThat(accountRepository.findAll()).hasSize(1);
    }

    @Test
    void givenOwnAccountValidPrivilegeWhenGetAccountInfoThen200() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name("priv").account(account).build());
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().name("priv2").accountProfile(accountProfile).build());

        final String url = String.format("/accounts/%s", account.getId());

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType", Matchers.equalTo("COMPANY")));
    }

    @Test
    void givenNotOwnAccountValidPrivilegeWhenGetAccountInfoThen403() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name("priv").account(account).build());
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().name("priv2").accountProfile(accountProfile).build());

        final String url = String.format("/accounts/%s", account.getId() + 1);

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isForbidden());
    }

}