package com.drop.here.backend.drophere.country;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
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

class CountryControllerTest extends IntegrationBaseClass {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @AfterEach
    void clean() {
        countryRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void givenAuthenticatedWhenGetAllActiveCountriesThenGet() throws Exception {
        //given
        final String url = "/countries";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE + "aaa").build());
        account.setPrivileges(List.of(privilege));

        countryRepository.saveAll(List.of(CountryDataGenerator.poland()));

        final TokenResponse token = jwtService.createToken(account);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name", Matchers.equalTo("Poland")));
    }

    @Test
    void givenNotAuthenticatedWhenGetAllActiveCountriesThen401() throws Exception {
        //given
        final String url = "/countries";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));

        countryRepository.saveAll(List.of(CountryDataGenerator.poland()));


        final TokenResponse token = jwtService.createToken(account);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}