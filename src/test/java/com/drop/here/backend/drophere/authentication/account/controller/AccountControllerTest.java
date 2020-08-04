package com.drop.here.backend.drophere.authentication.account.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends IntegrationBaseClass {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @AfterEach
    void cleanUp() {
        privilegeRepository.deleteAll();
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
        assertThat(privilegeRepository.findAll().get(0).getName()).isEqualTo("CREATE_COMPANY");
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

}