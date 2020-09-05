package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyCustomerRelationshipRepository;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private CompanyCustomerRelationshipRepository companyCustomerRelationshipRepository;

    private Company company;
    private Account account;
    private Drop drop;
    private Customer customer;
    private Country country;

    @BeforeEach
    void prepare() {
        country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        drop = dropRepository.save(DropDataGenerator.drop(1, company));
    }

    @AfterEach
    void cleanUp() {
        companyCustomerRelationshipRepository.deleteAll();
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
    void givenValidRequestBlockedCustomerWhenCreateDropMembershipThen403() throws Exception {
        //given
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        companyCustomerRelationshipRepository.save(relationship);

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
    void givenValidRequestWhenUpdateDropMembershipThenUpdate() throws Exception {
        //given
        final DropMembership dropMembership = DropDataGenerator.membership(drop, customer);
        dropMembership.setReceiveNotification(false);
        dropMembershipRepository.save(dropMembership);
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropMembershipManagementRequest.builder().receiveNotification(true).build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(dropMembershipRepository.findAll().get(0).isReceiveNotification()).isTrue();
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenUpdateDropMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(CUSTOMER_CREATED_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final DropMembership dropMembership = DropDataGenerator.membership(drop, customer);
        dropMembership.setReceiveNotification(false);
        dropMembershipRepository.save(dropMembership);
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropMembershipManagementRequest.builder().receiveNotification(true).build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropMembershipRepository.findAll().get(0).isReceiveNotification()).isFalse();
    }

    @Test
    void givenNotFoundMembershipWhenUpdateDropMembershipThen422() throws Exception {
        //given
        final String url = String.format("/drops/%s/companies/%s/memberships", drop.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(DropMembershipManagementRequest.builder().receiveNotification(true).build());
        dropRepository.save(drop);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
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
    void givenDropInUserRangeWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenDropInRangeDefinedByDropWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(70000);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "0")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenDropNotInRangeWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(20000);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "0")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenInvisibleDropWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        drop.setHidden(true);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenNotVisibleCompanyWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        company.setVisibilityStatus(CompanyVisibilityStatus.HIDDEN);
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenExistingDropByDropNamePrefixWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "spot")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenNotExistingDropByDropNameWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "spotter")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenExistingDropByCompanyNameWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "thai c")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenNotExistingDropByCompanyNameWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "thai kurwa")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenDropHiddenWithoutMembershipWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        drop.setHidden(true);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenDropHiddenWithMembershipWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setHidden(true);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenBlockedCustomerRelationshipWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        companyCustomerRelationshipRepository.save(relationship);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenActiveRelationshipDropWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE);
        companyCustomerRelationshipRepository.save(relationship);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenNotBlockedMembershipDropWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);
        final DropMembership membership = DropDataGenerator.membership(drop, customer);
        membership.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenBlockedMembershipDropWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);
        final DropMembership membership = DropDataGenerator.membership(drop, customer);
        membership.setMembershipStatus(DropMembershipStatus.BLOCKED);
        dropMembershipRepository.save(membership);
        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenMemberTrueAndHasMembershipDropWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .param("member", "true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenMemberTrueAndHasNotMembershipDropWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .param("member", "true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }

    @Test
    void givenMemberFalseAndHasNotMembershipDropWhenFindDropsThenFind() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .param("member", "false")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenMemberFalseAndHasMembershipDropWhenFindDropsThenEmpty() throws Exception {
        //given
        final String url = "/drops";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        drop.setName("Spot nr 1");
        drop.setXCoordinate(122.50);
        drop.setYCoordinate(50.00);
        drop.setEstimatedRadiusMeters(500);
        dropRepository.save(drop);

        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));

        //when
        //1degree == 111km
        final ResultActions result = mockMvc.perform(get(url)
                .param("xCoordinate", "122.00")
                .param("yCoordinate", "50.00")
                .param("namePrefix", "")
                .param("radius", "70000")
                .param("member", "false")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.empty()));
    }
}