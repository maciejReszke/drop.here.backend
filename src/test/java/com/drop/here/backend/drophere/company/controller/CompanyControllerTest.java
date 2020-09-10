package com.drop.here.backend.drophere.company.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyControllerTest extends IntegrationBaseClass {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Account account;
    private Company company;

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE)
                .account(account).build());
        final Image image = imageRepository.save(Image.builder().type(ImageType.CUSTOMER_IMAGE).bytes("bytes".getBytes()).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        company.setImage(image);
        company = companyRepository.save(company);
    }

    @AfterEach
    void cleanUp() {
        customerRepository.deleteAll();
        privilegeRepository.deleteAll();
        companyRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void givenExistingCompanyImageWhenGetCompanyThenGet() throws Exception {
        //given
        final String url = String.format("/companies/%s/images", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    void givenCNotExistingCompanyImageWhenGetImageThen404() throws Exception {
        //given
        company.setImage(null);
        companyRepository.save(company);
        final String url = String.format("/companies/%s/images", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }
}