package com.drop.here.backend.drophere.authentication.account.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
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

    @AfterEach
    void cleanUp() {
        accountRepository.deleteAll();
    }

    @Test
    void givenValidRequestWhenCreateAccountThenCreate() throws Exception {
        //given
        final AccountCreationRequest request = AccountDataGenerator.accountCreationRequest(1);
        final String json = objectMapper.writeValueAsString(request);

        final String url = "/accounts";

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.tokenValidUntil", Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.accountType", Matchers.equalTo("COMPANY")));

        assertThat(accountRepository.findAll()).hasSize(1);
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