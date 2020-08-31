package com.drop.here.backend.drophere.customer.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
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
        privilegeRepository.deleteAll();
        customerRepository.deleteAll();
        accountRepository.deleteAll();
        imageRepository.deleteAll();
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
    void givenCustomerNotOwnExistingCustomerImageWhenGetCustomerThenError() throws Exception {
        //given
        final String url = String.format("/customers/%s/images", customer.getId() + 1);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());
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