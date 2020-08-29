package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ProductUnitControllerTest extends IntegrationBaseClass {
    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @AfterEach
    void clean() {
        productUnitRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void givenAuthenticatedWhenGetAllUnitsThenGet() throws Exception {
        //given
        final String url = "/units";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE + "aaa").build());
        account.setPrivileges(List.of(privilege));

        productUnitRepository.saveAll(List.of(
                ProductUnit.builder().name("bbunit").createdAt(LocalDateTime.now()).build(),
                ProductUnit.builder().name("aaunit").createdAt(LocalDateTime.now()).build()
        ));

        final TokenResponse token = jwtService.createToken(account);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", Matchers.equalTo("aaunit")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name", Matchers.equalTo("bbunit")));
    }

    @Test
    void givenNotAuthenticatedWhenGetAllUnitsThen401() throws Exception {
        //given
        final String url = "/units";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));

        productUnitRepository.saveAll(List.of(
                ProductUnit.builder().name("bbunit").createdAt(LocalDateTime.now()).build(),
                ProductUnit.builder().name("aaunit").createdAt(LocalDateTime.now()).build()
        ));

        final TokenResponse token = jwtService.createToken(account);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}