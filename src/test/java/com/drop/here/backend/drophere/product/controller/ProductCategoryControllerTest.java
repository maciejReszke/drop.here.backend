package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.authentication.token.TokenResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductUnitDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ProductCategoryControllerTest extends IntegrationBaseClass {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CountryRepository countryRepository;

    private ProductUnit productUnit;
    private Country country;


    @BeforeEach
    void prepare() {
        country = countryRepository.save(CountryDataGenerator.poland());
        productUnit = productUnitRepository.save(ProductUnitDataGenerator.productUnit(1));
    }

    @AfterEach
    void clean() {
        productRepository.deleteAll();
        productUnitRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenAuthenticatedWhenGetAllCategoriesThenGet() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE + "aaa").build());
        account.setPrivileges(List.of(privilege));
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final String url = String.format("/companies/%s/categories", company.getUid());
        final Product product1 = ProductDataGenerator.product(1, productUnit, company);
        product1.setCategory("bbcategory");
        productRepository.save(product1);
        final Product product2 = ProductDataGenerator.product(2, productUnit, company);
        product2.setCategory("aacategory");
        productRepository.save(product2);

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
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final String url = String.format("/companies/%s/categories", company.getUid());

        final Product product1 = ProductDataGenerator.product(1, productUnit, company);
        product1.setCategory("bbcategory");
        productRepository.save(product1);
        final Product product2 = ProductDataGenerator.product(2, productUnit, company);
        product2.setCategory("aacategory");
        productRepository.save(product2);


        final TokenResponse token = jwtService.createToken(account);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getToken()));

        //then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}