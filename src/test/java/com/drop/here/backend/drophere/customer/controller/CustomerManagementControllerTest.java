package com.drop.here.backend.drophere.customer.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.CUSTOMER_CREATED_PRIVILEGE;
import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerManagementControllerTest extends IntegrationBaseClass {

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

    @BeforeEach
    void prepare() {
        account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE)
                .account(account).build());
    }

    @AfterEach
    void cleanUp() {
        privilegeRepository.deleteAll();
        customerRepository.deleteAll();
        accountRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void givenValidRequestNotExistingCustomerWhenUpdateCustomerThenUpdate() throws Exception {
        //given
        final String url = "/management/customers";
        final String json = objectMapper.writeValueAsString(CustomerDataGenerator.managementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(customerRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(2);
    }

    @Test
    void givenValidRequestExistingCustomerWhenUpdateCustomerThenUpdate() throws Exception {
        //given
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final String url = "/management/customers";
        final CustomerManagementRequest request = CustomerDataGenerator.managementRequest(1);
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(customerRepository.findAll().get(0).getFirstName()).isEqualTo(request.getFirstName());
        assertThat(privilegeRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenUpdateCustomerThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.COMPANY_BASIC_MANAGEMENT_PRIVILEGE);
        privilegeRepository.save(privilege);

        final String url = "/management/customers";
        final String json = objectMapper.writeValueAsString(CustomerDataGenerator.managementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(customerRepository.findAll()).isEmpty();
    }

    @Test
    void givenNotExistingCustomerWhenGetCustomerThenGet() throws Exception {
        //given
        final String url = "/management/customers";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.registered", Matchers.equalTo(false)))
                .andExpect(jsonPath("$.firstName", Matchers.emptyOrNullString()));
    }

    @Test
    void givenExistingCustomerWhenGetCustomerThenGet() throws Exception {
        //given
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final String url = "/management/customers";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.registered", Matchers.equalTo(true)))
                .andExpect(jsonPath("$.firstName", Matchers.equalTo("firstCustomerName1")));
    }

    @Test
    void givenInvalidPrivilegeWhenGetCustomerThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE);
        privilegeRepository.save(privilege);

        final String url = "/management/customers";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestNotExistingImageWhenUpdateImageThenUpdate() throws Exception {
        //given
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final String url = "/management/customers/images";
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(CUSTOMER_CREATED_PRIVILEGE);
        privilegeRepository.save(privilege);

        //when
        final ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk());
        assertThat(imageRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestExistingImageWhenUpdateImageThenUpdate() throws Exception {
        //given
        final Image image = imageRepository.save(Image.builder().bytes("aa".getBytes()).type(ImageType.CUSTOMER_IMAGE).build());
        final Customer customer = CustomerDataGenerator.customer(1, account);
        customer.setImage(image);
        customerRepository.save(customer);
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(CUSTOMER_CREATED_PRIVILEGE);
        privilegeRepository.save(privilege);
        final String url = "/management/customers/images";
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        //when
        final ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk());
        assertThat(imageRepository.findAll()).hasSize(1);
        assertThat(imageRepository.findById(image.getId())).isEmpty();
    }

    @Test
    void givenValidRequestInvalidPrivilegesWhenUpdateImageThen403() throws Exception {
        //given
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final String url = "/management/customers/images";
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        //when
        final ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isForbidden());
        assertThat(imageRepository.findAll()).isEmpty();
    }
}