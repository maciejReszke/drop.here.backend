package com.drop.here.backend.drophere.customer.controller;

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
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerTest extends IntegrationBaseClass {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private DropMembershipRepository dropMembershipRepository;

    @Autowired
    private CountryRepository countryRepository;


    private Account account;
    private Customer customer;

    @BeforeEach
    void prepare() {
        account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                .account(account).build());
        final Image image = imageRepository.save(Image.builder().type(ImageType.CUSTOMER_IMAGE).bytes("bytes".getBytes()).build());
        customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        customer.setImage(image);
        customerRepository.save(customer);
    }

    @AfterEach
    void cleanUp() {
        dropMembershipRepository.deleteAll();
        dropRepository.deleteAll();
        privilegeRepository.deleteAll();
        companyRepository.deleteAll();
        customerRepository.deleteAll();
        accountRepository.deleteAll();
        imageRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenCustomerExistingCustomerImageWhenGetCustomerImageThenGet() throws Exception {
        //given
        final String url = String.format("/customers/%s/images", customer.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    void givenCompaniesCustomerExistingCustomerImageWhenGetCustomerThenGet() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                .account(account).build());
        final String url = String.format("/customers/%s/images", customer.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    void givenCompaniesDifferentCustomerExistingCustomerImageWhenGetCustomerThenForbidden() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                .account(account).build());
        final String url = String.format("/customers/%s/images", customer.getId() + 1L);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenDifferentCompaniesCustomerExistingCustomerImageWhenGetCustomerThenForbidden() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Account account2 = accountRepository.save(AccountDataGenerator.companyAccount(2));
        final Company company2 = companyRepository.save(CompanyDataGenerator.company(2, account2, country));
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company2));
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final String url = String.format("/customers/%s/images", customer.getId());
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                .account(account).build());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenCustomerNotOwnExistingCustomerImageWhenGetCustomerThen403() throws Exception {
        //given
        final String url = String.format("/customers/%s/images", customer.getId() + 1);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenCustomerNotExistingCustomerImageWhenGetCustomerImageThen404() throws Exception {
        //given
        customer.setImage(null);
        customerRepository.save(customer);
        final String url = String.format("/customers/%s/images", customer.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

}