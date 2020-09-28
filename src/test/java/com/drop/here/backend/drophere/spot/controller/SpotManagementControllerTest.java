package com.drop.here.backend.drophere.spot.controller;

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
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
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

class SpotManagementControllerTest extends IntegrationBaseClass {

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
    private SpotRepository spotRepository;

    @Autowired
    private SpotMembershipRepository spotMembershipRepository;

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
        spotMembershipRepository.deleteAll();
        spotRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenCreateSpotThenCreate() throws Exception {
        //given
        final String url = String.format("/companies/%s/spots", company.getUid());
        final String json = objectMapper.writeValueAsString(SpotDataGenerator.spotManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(spotRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenCreateSpotThen403() throws Exception {
        //given
        final String url = String.format("/companies/%s/spots", company.getUid() + "i");
        final String json = objectMapper.writeValueAsString(SpotDataGenerator.spotManagementRequest(1));


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenCreateSpotThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final String url = String.format("/companies/%s/spots", company.getUid());
        final String json = objectMapper.writeValueAsString(SpotDataGenerator.spotManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenCreateSpotThen422() throws Exception {
        //given
        final String url = String.format("/companies/%s/spots", company.getUid());
        final SpotManagementRequest request = SpotDataGenerator.spotManagementRequest(1);
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
        assertThat(spotRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateSpotThenUpdate() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid(), spot.getId());
        final SpotManagementRequest request = SpotDataGenerator.spotManagementRequest(1);
        request.setName("newName123");
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(spotRepository.findAll()).hasSize(1);
        assertThat(spotRepository.findAll().get(0).getName()).isEqualTo(request.getName());
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateSpotThen403() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid() + "i", spot.getId());
        final SpotManagementRequest request = SpotDataGenerator.spotManagementRequest(1);
        request.setName("newName123");
        final String json = objectMapper.writeValueAsString(SpotDataGenerator.spotManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotRepository.findAll()).hasSize(1);
        assertThat(spotRepository.findAll().get(0).getName()).isNotEqualTo(request.getName());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenUpdateSpotThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid(), spot.getId());
        final SpotManagementRequest request = SpotDataGenerator.spotManagementRequest(1);
        request.setName("newName123");
        final String json = objectMapper.writeValueAsString(SpotDataGenerator.spotManagementRequest(1));

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotRepository.findAll()).hasSize(1);
        assertThat(spotRepository.findAll().get(0).getName()).isNotEqualTo(request.getName());
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenUpdateSpotThen422() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid(), spot.getId());
        final SpotManagementRequest request = SpotDataGenerator.spotManagementRequest(1);
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
        assertThat(spotRepository.findAll()).hasSize(1);
        assertThat(spotRepository.findAll().get(0).getName()).isNotEqualTo(request.getName());
    }

    @Test
    void givenSpotWithoutMembershipsValidRequestOwnCompanyOperationWhenDeleteSpotThenDelete() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid(), spot.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(spotRepository.findAll()).isEmpty();
        assertThat(spotMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenSpotWithMembershipsValidRequestOwnCompanyOperationWhenDeleteSpotThenDelete() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        final String url = String.format("/companies/%s/spots/%s", company.getUid(), spot.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(spotRepository.findAll()).isEmpty();
        assertThat(spotMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenDeleteSpotThen403() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid() + "i", spot.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenDeleteSpotThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid(), spot.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotRepository.findAll()).hasSize(1);
    }

    @Test
    void givenNotExistingSpotRequestOwnCompanyOperationWhenUpdateDeleteThen422() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots/%s", company.getUid(), spot.getId() + 1);

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        assertThat(spotRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindSpotsThenFind() throws Exception {
        //given
        final Spot spot1 = spotRepository.save(SpotDataGenerator.spot(1, company).toBuilder()
                .name("ryneczek").build());
        final Spot spot2 = spotRepository.save(SpotDataGenerator.spot(2, company).toBuilder()
                .name("stoisko").build());
        final String url = String.format("/companies/%s/spots", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].id", Matchers.equalTo(spot1.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", Matchers.equalTo(spot2.getId().intValue())));
    }

    @Test
    void givenValidRequestByNameOwnCompanyOperationWhenFindSpotsThenFind() throws Exception {
        //given
        final Spot spot1 = spotRepository.save(SpotDataGenerator.spot(1, company).toBuilder()
                .name("ryneczek").build());
        spotRepository.save(SpotDataGenerator.spot(2, company).toBuilder()
                .name("stoisko").build());
        final String url = String.format("/companies/%s/spots", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "ryne")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].id", Matchers.equalTo(spot1.getId().intValue())));
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenFindSpotsThen403() throws Exception {
        //given
        spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots", company.getUid() + "i");

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenFindSpotsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        spotRepository.save(SpotDataGenerator.spot(1, company));
        final String url = String.format("/companies/%s/spots", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateSpotMembershipThenUpdate() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final SpotMembership membership = spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        membership.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/spots/%s/memberships/%s", company.getUid(), spot.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.BLOCKED.name())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(spotMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(SpotMembershipStatus.BLOCKED);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateSpotMembershipThen403() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final SpotMembership membership = spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        membership.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/spots/%s/memberships/%s", company.getUid() + "kek", spot.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.BLOCKED.name())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(spotMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(SpotMembershipStatus.ACTIVE);
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenUpdateSpotMembershipThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final SpotMembership membership = spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        membership.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/spots/%s/memberships/%s", company.getUid(), spot.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.BLOCKED.name())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(spotMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(SpotMembershipStatus.ACTIVE);
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenUpdateSpotMembershipThen422() throws Exception {
        //given
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final SpotMembership membership = spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        membership.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership);

        final String url = String.format("/companies/%s/spots/%s/memberships/%s", company.getUid(), spot.getId(), membership.getId());
        final String json = objectMapper.writeValueAsString(SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus("kaka")
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

        assertThat(spotMembershipRepository.findById(membership.getId()).orElseThrow().getMembershipStatus())
                .isEqualTo(SpotMembershipStatus.ACTIVE);
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindMembershipsWithoutParamsThenFind() throws Exception {
        //given
        final Spot spot = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/spots/%s/memberships", company.getUid(), spot.getId());

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
        final Spot spot = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/spots/%s/memberships", company.getUid(), spot.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("membershipStatus", SpotMembershipStatus.ACTIVE.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindMembershipsWithNameParamThenFind() throws Exception {
        //given
        final Spot spot = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/spots/%s/memberships", company.getUid(), spot.getId());

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
        final Spot spot = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/spots/%s/memberships", company.getUid(), spot.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("customerName", "Kro")
                .param("membershipStatus", SpotMembershipStatus.ACTIVE.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenGetSpotMembershipsThen403() throws Exception {
        //given
        final Spot spot = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/spots/%s/memberships", company.getUid() + "kek", spot.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenFindSpotMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        //given
        final Spot spot = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/spots/%s/memberships", company.getUid(), spot.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenFindSpotMembershipThen404() throws Exception {
        //given
        final Spot spot = prepareSearchingMembershipData();

        final String url = String.format("/companies/%s/spots/%s/memberships", company.getUid(), spot.getId() + 15);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @NotNull
    private Spot prepareSearchingMembershipData() {
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final Customer customer1 = CustomerDataGenerator.customer(1, account);
        customer1.setFirstName("Maciej");
        customer1.setLastName("Krostka");
        customerRepository.save(customer1);
        final SpotMembership membership1 = spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer1));
        membership1.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership1);

        final Account customer2Account = accountRepository.save(AccountDataGenerator.customerAccount(2));
        final Customer customer2 = CustomerDataGenerator.customer(2, customer2Account);
        customer2.setFirstName("Karol");
        customer2.setLastName("Kromka");
        customerRepository.save(customer2);
        final SpotMembership membership2 = spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer2));
        membership2.setMembershipStatus(SpotMembershipStatus.BLOCKED);
        spotMembershipRepository.save(membership2);

        final Account customer3Account = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer customer3 = CustomerDataGenerator.customer(3, customer3Account);
        customer3.setFirstName("Michal");
        customer3.setLastName("Inny");
        customerRepository.save(customer3);
        final SpotMembership membership3 = spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer3));
        membership3.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership3);

        final Spot spot2 = spotRepository.save(SpotDataGenerator.spot(2, company));
        final SpotMembership membership4 = spotMembershipRepository.save(SpotDataGenerator.membership(spot2, customer3));
        membership4.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership4);
        return spot;
    }


}