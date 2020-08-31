package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.CUSTOMER_CREATED_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DropUserControllerTest extends IntegrationBaseClass {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private DropMembershipRepository dropMembershipRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Company company;
    private Account account;
    private Drop drop;
    private Customer customer;

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        drop = dropRepository.save(DropDataGenerator.drop(1, company));
    }

    @AfterEach
    void cleanUp() {
        dropMembershipRepository.deleteAll();
        dropRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenValidRequestWithoutPasswordVisibleCompanyWhenCreateDropMembershipThenCreate() throws Exception {
        //given
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);
        drop.setRequiresPassword(false);
        dropRepository.save(drop);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(dropMembershipRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestWithPasswordVisibleCompanyWhenCreateDropMembershipThenCreate() throws Exception {
        //given
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);
        drop.setRequiresPassword(true);
        drop.setPassword("pass");
        dropRepository.save(drop);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(dropMembershipRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotVisibleCompanyWhenCreateDropMembershipThen403() throws Exception {
        //given
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.HIDDEN);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestVisibleCompanyInvalidPrivilegeWhenCreateDropMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(CUSTOMER_CREATED_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenCreateDropThen422() throws Exception {
        //given
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        drop.setRequiresPassword(true);
        drop.setPassword("sadada");
        dropRepository.save(drop);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        assertThat(dropMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestWhenDeleteDropMembershipThenDelete() throws Exception {
        //given
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(dropMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenDeleteDropMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(CUSTOMER_CREATED_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropMembershipRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenInvalidRequestNotExistingDropMembershipWhenDeleteDropThen404() throws Exception {
        //given
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid() + "kek", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        assertThat(dropMembershipRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenEmptyNameWhenFindMembershipsThenFind() throws Exception {
        //given
        final String url = "/drops/memberships";
        drop.setName("dropName1");
        dropRepository.save(drop);
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final Drop secondDrop = dropRepository.save(DropDataGenerator.drop(2, company));
        secondDrop.setName("chromName1");
        dropRepository.save(secondDrop);
        dropMembershipRepository.save(DropDataGenerator.membership(secondDrop, customer));
        final Account anotherAccount = accountRepository.save(AccountDataGenerator.customerAccount(2));
        final Customer anotherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, anotherAccount));
        final Drop anotherDrop = dropRepository.save(DropDataGenerator.drop(3, company));
        dropMembershipRepository.save(DropDataGenerator.membership(anotherDrop, anotherCustomer));

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenPrefixNameWhenFindMembershipsThenFind() throws Exception {
        //given
        final String url = "/drops/memberships";
        drop.setName("dropName1");
        dropRepository.save(drop);
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final Drop secondDrop = dropRepository.save(DropDataGenerator.drop(2, company));
        secondDrop.setName("chromName1");
        dropRepository.save(secondDrop);
        dropMembershipRepository.save(DropDataGenerator.membership(secondDrop, customer));
        final Account anotherAccount = accountRepository.save(AccountDataGenerator.customerAccount(2));
        final Customer anotherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, anotherAccount));
        final Drop anotherDrop = dropRepository.save(DropDataGenerator.drop(3, company));
        dropMembershipRepository.save(DropDataGenerator.membership(anotherDrop, anotherCustomer));

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "chrom")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenInvalidPrivilegeWhenFindMembershipsThen403() throws Exception {
        //given
        final String url = "/drops/memberships";
        drop.setName("dropName1");
        dropRepository.save(drop);
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final Drop secondDrop = dropRepository.save(DropDataGenerator.drop(2, company));
        secondDrop.setName("chromName1");
        dropRepository.save(secondDrop);
        dropMembershipRepository.save(DropDataGenerator.membership(secondDrop, customer));
        final Account anotherAccount = accountRepository.save(AccountDataGenerator.customerAccount(2));
        final Customer anotherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, anotherAccount));
        final Drop anotherDrop = dropRepository.save(DropDataGenerator.drop(3, company));
        dropMembershipRepository.save(DropDataGenerator.membership(anotherDrop, anotherCustomer));
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(CUSTOMER_CREATED_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }
}