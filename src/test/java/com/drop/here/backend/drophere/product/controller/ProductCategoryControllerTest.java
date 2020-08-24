package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.repository.ProductCategoryRepository;
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

class ProductCategoryControllerTest extends IntegrationBaseClass {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @AfterEach
    void clean() {
        productCategoryRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void givenAuthenticatedWhenGetAllCategoriesThenGet() throws Exception {
        //given
        final String url = "/categories";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1, null));
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE + "aaa").build());
        account.setPrivileges(List.of(privilege));

        productCategoryRepository.saveAll(List.of(
                ProductCategory.builder().name("bbcategory").createdAt(LocalDateTime.now()).build(),
                ProductCategory.builder().name("aacategory").createdAt(LocalDateTime.now()).build()
        ));

        final TokenResponse token = jwtService.createToken(account);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", Matchers.equalTo("aacategory")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name", Matchers.equalTo("bbcategory")));
    }

    @Test
    void givenNotAuthenticatedWhenGetAllCategoriesThen401() throws Exception {
        //given
        final String url = "/categories";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1, null));

        productCategoryRepository.saveAll(List.of(
                ProductCategory.builder().name("bbcategory").createdAt(LocalDateTime.now()).build(),
                ProductCategory.builder().name("aacategory").createdAt(LocalDateTime.now()).build()
        ));

        final TokenResponse token = jwtService.createToken(account);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}