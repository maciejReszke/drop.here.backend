package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.drop.dto.DropCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DropManagementControllerTest extends IntegrationBaseClass {

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

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
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
    void givenValidRequestOwnCompanyOperationWhenCreateDropThenCreate() throws Exception {
        //given
        final String url = String.format("/companies/%s/drops", company.getUid());
        final String json = objectMapper.writeValueAsString(DropDataGenerator.dropManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(dropRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenCreateDropThen403() throws Exception {
        //given
        final String url = String.format("/companies/%s/drops", company.getUid() + "i");
        final String json = objectMapper.writeValueAsString(DropDataGenerator.dropManagementRequest(1));


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenCreateDropThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final String url = String.format("/companies/%s/drops", company.getUid());
        final String json = objectMapper.writeValueAsString(DropDataGenerator.dropManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenCreateDropThen422() throws Exception {
        //given
        final String url = String.format("/companies/%s/drops", company.getUid());
        final DropManagementRequest request = DropDataGenerator.dropManagementRequest(1);
        request.setPassword(null);
        request.setRequiresPassword(true);
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        assertThat(dropRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateDropThenUpdate() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId());
        final DropManagementRequest request = DropDataGenerator.dropManagementRequest(1);
        request.setName("newName123");
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(dropRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll().get(0).getName()).isEqualTo(request.getName());
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateDropThen403() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid() + "i", drop.getId());
        final DropManagementRequest request = DropDataGenerator.dropManagementRequest(1);
        request.setName("newName123");
        final String json = objectMapper.writeValueAsString(DropDataGenerator.dropManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll().get(0).getName()).isNotEqualTo(request.getName());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenUpdateDropThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId());
        final DropManagementRequest request = DropDataGenerator.dropManagementRequest(1);
        request.setName("newName123");
        final String json = objectMapper.writeValueAsString(DropDataGenerator.dropManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll().get(0).getName()).isNotEqualTo(request.getName());
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenUpdateDropThen422() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId());
        final DropManagementRequest request = DropDataGenerator.dropManagementRequest(1);
        request.setName("newName123");
        request.setPassword(null);
        request.setRequiresPassword(true);
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        assertThat(dropRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll().get(0).getName()).isNotEqualTo(request.getName());
    }

    @Test
    void givenDropWithoutMembershipsValidRequestOwnCompanyOperationWhenDeleteDropThenDelete() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(dropMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenDropWithMembershipsValidRequestOwnCompanyOperationWhenDeleteDropThenDelete() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(dropMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenDeleteDropThen403() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid() + "i", drop.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenDeleteDropThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropRepository.findAll()).hasSize(1);
    }

    @Test
    void givenNotExistingDropRequestOwnCompanyOperationWhenUpdateDeleteThen422() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId() + 1);

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        assertThat(dropRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindDropsThenFind() throws Exception {
        //given
        final Drop drop1 = dropRepository.save(DropDataGenerator.drop(1, company).toBuilder()
                .name("ryneczek").build());
        final Drop drop2 = dropRepository.save(DropDataGenerator.drop(2, company).toBuilder()
                .name("stoisko").build());
        final String url = String.format("/companies/%s/drops", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].id", Matchers.equalTo(drop1.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", Matchers.equalTo(drop2.getId().intValue())));
    }

    @Test
    void givenValidRequestByNameOwnCompanyOperationWhenFindDropsThenFind() throws Exception {
        //given
        final Drop drop1 = dropRepository.save(DropDataGenerator.drop(1, company).toBuilder()
                .name("ryneczek").build());
        dropRepository.save(DropDataGenerator.drop(2, company).toBuilder()
                .name("stoisko").build());
        final String url = String.format("/companies/%s/drops", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "ryne")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.equalTo(drop1.getId().intValue())));
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenFindDropsThen403() throws Exception {
        //given
        dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops", company.getUid() + "i");

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenFindDropsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateDropMembershipThenUpdate() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final DropMembership membership = dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        membership.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/drops/%s/memberships/%s", company.getUid(), drop.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.BLOCKED.name())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(dropMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(DropMembershipStatus.BLOCKED);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateDropMembershipThen403() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final DropMembership membership = dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        membership.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/drops/%s/memberships/%s", company.getUid() + "kek", drop.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.BLOCKED.name())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(dropMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(DropMembershipStatus.ACTIVE);
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenUpdateDropMembershipThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final DropMembership membership = dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        membership.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/drops/%s/memberships/%s", company.getUid(), drop.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.BLOCKED.name())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(dropMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(DropMembershipStatus.ACTIVE);
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenUpdateDropMembershipThen422() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final DropMembership membership = dropMembershipRepository.save(DropDataGenerator.membership(drop, customer));
        membership.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/drops/%s/memberships/%s", company.getUid(), drop.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(DropCompanyMembershipManagementRequest.builder()
                .membershipStatus("kaka")
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

        assertThat(dropMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(DropMembershipStatus.ACTIVE);
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindMembershipsWithoutParamsThenFind() throws Exception {
        //given
        final Drop drop = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/drops/%s/memberships", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(3)));
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindMembershipsWithMembershipStatusParamThenFind() throws Exception {
        //given
        final Drop drop = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/drops/%s/memberships", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("membershipStatus", DropMembershipStatus.ACTIVE.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindMembershipsWithNameParamThenFind() throws Exception {
        //given
        final Drop drop = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/drops/%s/memberships", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("customerName", "Kro")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindMembershipsWithNameAndMembershipParamsThenFind() throws Exception {
        //given
        final Drop drop = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/drops/%s/memberships", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("customerName", "Kro")
                .param("membershipStatus", DropMembershipStatus.ACTIVE.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenGetDropMembershipsThen403() throws Exception {
        //given
        final Drop drop = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/drops/%s/memberships", company.getUid() + "kek", drop.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenFindDropMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        //given
        final Drop drop = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/drops/%s/memberships", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenFindDropMembershipThen404() throws Exception {
        //given
        final Drop drop = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/drops/%s/memberships", company.getUid(), drop.getId() + 15);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @NotNull
    private Drop prepareSearchingMembershipData() {
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final Customer customer1 = CustomerDataGenerator.customer(1, account);
        customer1.setFirstName("Maciej");
        customer1.setLastName("Krostka");
        customerRepository.save(customer1);
        final DropMembership membership1 = dropMembershipRepository.save(DropDataGenerator.membership(drop, customer1));
        membership1.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership1);

        final Account customer2Account = accountRepository.save(AccountDataGenerator.customerAccount(2));
        final Customer customer2 = CustomerDataGenerator.customer(2, customer2Account);
        customer2.setFirstName("Karol");
        customer2.setLastName("Kromka");
        customerRepository.save(customer2);
        final DropMembership membership2 = dropMembershipRepository.save(DropDataGenerator.membership(drop, customer2));
        membership2.setMembershipStatus(DropMembershipStatus.BLOCKED);
        dropMembershipRepository.save(membership2);

        final Account customer3Account = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer customer3 = CustomerDataGenerator.customer(3, customer3Account);
        customer3.setFirstName("Michal");
        customer3.setLastName("Inny");
        customerRepository.save(customer3);
        final DropMembership membership3 = dropMembershipRepository.save(DropDataGenerator.membership(drop, customer3));
        membership3.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership3);

        final Drop drop2 = dropRepository.save(DropDataGenerator.drop(2, company));
        final DropMembership membership4 = dropMembershipRepository.save(DropDataGenerator.membership(drop2, customer3));
        membership4.setMembershipStatus(DropMembershipStatus.ACTIVE);
        dropMembershipRepository.save(membership4);
        return drop;
    }


}