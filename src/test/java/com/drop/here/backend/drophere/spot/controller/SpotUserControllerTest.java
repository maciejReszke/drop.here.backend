package com.drop.here.backend.drophere.spot.controller;

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
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotMembershipManagementRequest;
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
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.CUSTOMER_CREATED_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SpotUserControllerTest extends IntegrationBaseClass {

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

    @Autowired
    private CompanyCustomerRelationshipRepository companyCustomerRelationshipRepository;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private RouteRepository routeRepository;

    private Company company;
    private Account account;
    private Spot spot;
    private Customer customer;
    private Country country;

    @BeforeEach
    void prepare() {
        country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        spot = spotRepository.save(SpotDataGenerator.spot(1, company));
    }

    @AfterEach
    void cleanUp() {
        dropRepository.deleteAll();
        routeRepository.deleteAll();
        companyCustomerRelationshipRepository.deleteAll();
        spotMembershipRepository.deleteAll();
        spotRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenValidRequestWithoutPasswordVisibleCompanyWhenCreateSpotMembershipThenCreate() throws Exception {
        //given
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);
        spot.setRequiresPassword(false);
        spotRepository.save(spot);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(spotMembershipRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestWithPasswordVisibleCompanyWhenCreateSpotMembershipThenCreate() throws Exception {
        //given
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);
        spot.setRequiresPassword(true);
        spot.setPassword("pass");
        spotRepository.save(spot);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(spotMembershipRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotVisibleCompanyWhenCreateSpotMembershipThen403() throws Exception {
        //given
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.HIDDEN);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestBlockedCustomerWhenCreateSpotMembershipThen403() throws Exception {
        //given
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotJoinRequest.builder().password("pass").build());
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
        assertThat(spotMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestVisibleCompanyInvalidPrivilegeWhenCreateSpotMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(CUSTOMER_CREATED_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenCreateSpotThen422() throws Exception {
        //given
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotJoinRequest.builder().password("pass").build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        spot.setRequiresPassword(true);
        spot.setPassword("sadada");
        spotRepository.save(spot);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        assertThat(spotMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestWhenUpdateSpotMembershipThenUpdate() throws Exception {
        //given
        final SpotMembership spotMembership = SpotDataGenerator.membership(spot, customer);
        spotMembership.setReceiveFinishedNotifications(false);
        spotMembershipRepository.save(spotMembership);
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotMembershipManagementRequest.builder()
                .receiveFinishedNotifications(true).build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(spotMembershipRepository.findAll().get(0).isReceiveFinishedNotifications()).isTrue();
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenUpdateSpotMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(CUSTOMER_CREATED_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final SpotMembership spotMembership = SpotDataGenerator.membership(spot, customer);
        spotMembership.setReceiveFinishedNotifications(false);
        spotMembershipRepository.save(spotMembership);
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotMembershipManagementRequest.builder()
                .receiveFinishedNotifications(true).build());
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotMembershipRepository.findAll().get(0).isReceiveFinishedNotifications()).isFalse();
    }

    @Test
    void givenNotFoundMembershipWhenUpdateSpotMembershipThen422() throws Exception {
        //given
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        final String json = objectMapper.writeValueAsString(SpotMembershipManagementRequest.builder()
                .receiveFinishedNotifications(true).build());
        spotRepository.save(spot);
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
    void givenValidRequestWhenDeleteSpotMembershipThenDelete() throws Exception {
        //given
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(spotMembershipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenDeleteSpotMembershipsThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(CUSTOMER_CREATED_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid(), company.getUid());
        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(spotMembershipRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenInvalidRequestNotExistingSpotMembershipWhenDeleteSpotThen404() throws Exception {
        //given
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        final String url = String.format("/spots/%s/companies/%s/memberships", spot.getUid() + "kek", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        assertThat(spotMembershipRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenSpotInUserRangeWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenSpotInRangeDefinedBySpotWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(70000);
        spotRepository.save(spot);

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
    void givenSpotNotInRangeWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(20000);
        spotRepository.save(spot);

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
    void givenInvisibleSpotWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spot.setHidden(true);
        spotRepository.save(spot);

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
    void givenNotVisibleCompanyWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        company.setVisibilityStatus(CompanyVisibilityStatus.HIDDEN);
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenExistingSpotBySpotNamePrefixWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenNotExistingSpotBySpotNameWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenExistingSpotByCompanyNameWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenNotExistingSpotByCompanyNameWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenSpotHiddenWithoutMembershipWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spot.setHidden(true);
        spotRepository.save(spot);

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
    void givenSpotHiddenWithMembershipWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setHidden(true);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));

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
    void givenBlockedCustomerRelationshipWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);
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
    void givenActiveRelationshipSpotWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);
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
    void givenNotBlockedMembershipSpotWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership);

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
    void givenBlockedMembershipSpotWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.BLOCKED);
        spotMembershipRepository.save(membership);
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
    void givenMemberTrueAndHasMembershipSpotWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));

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
    void givenMemberTrueAndHasNotMembershipSpotWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenMemberFalseAndHasNotMembershipSpotWhenFindSpotsThenFind() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

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
    void givenMemberFalseAndHasMembershipSpotWhenFindSpotsThenEmpty() throws Exception {
        //given
        final String url = "/spots";

        company.setName("Thai cuisine");
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spot.setXCoordinate(122.50);
        spot.setYCoordinate(50.00);
        spot.setEstimatedRadiusMeters(500);
        spotRepository.save(spot);

        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));

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

    @Test
    void givenExistingSpotVisibleCompanyNotHiddenNotMembershipNotBlockedWhenFindSpotThenFind() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.drops", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.spot.name", Matchers.equalTo("Spot nr 1")));
    }

    @Test
    void givenExistingSpotVisibleCompanyHiddenMembershipActiveWhenFindSpotThenFind() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spot.setHidden(true);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.drops", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.spot.name", Matchers.equalTo("Spot nr 1")));
    }

    @Test
    void givenExistingSpotVisibleCompanyHiddenMembershipPendingWhenFindSpotThenNotFound() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spot.setHidden(true);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.PENDING);
        spotMembershipRepository.save(membership);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingSpotVisibleCompanyNotHiddenMembershipPendingWhenFindSpotThenFind() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spot.setHidden(false);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.PENDING);
        spotMembershipRepository.save(membership);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.drops", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.spot.name", Matchers.equalTo("Spot nr 1")));
    }

    @Test
    void givenExistingSpotVisibleCompanyNotHiddenMembershipBlockedWhenFindSpotThenNotFound() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spot.setHidden(false);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.BLOCKED);
        spotMembershipRepository.save(membership);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingSpotVisibleCompanyHiddenMembershipBlockedWhenFindSpotThenNotFound() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spot.setHidden(true);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.BLOCKED);
        spotMembershipRepository.save(membership);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenNotExistingSpotVisibleCompanyNotHiddenNotMembershipWhenFindSpotThen404() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid() + "Keepo");

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingSpotNotVisibleCompanyNotHiddenNotMembershipWhenFindSpotThen404() throws Exception {
        //given
        company.setVisibilityStatus(CompanyVisibilityStatus.HIDDEN);
        companyRepository.save(company);
        spot.setName("Spot nr 1");
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingSpotVisibleCompanyHiddenNotMembershipWhenFindSpotThen404() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spot.setHidden(true);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingSpotVisibleCompanyNotHiddenNotMembershipBlockedWhenFindSpotThen404() throws Exception {
        //given
        spot.setName("Spot nr 1");
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        companyCustomerRelationshipRepository.save(relationship);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingSpotVisibleCompanyNotHiddenNotMembershipActiveRelationShipWhenFindSpotThenFind() throws Exception {
        //given
        spot.setName("Spot nr 1");
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE);
        companyCustomerRelationshipRepository.save(relationship);
        spotRepository.save(spot);
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final Drop drop1 = DropDataGenerator.drop(1, route, spot);
        drop1.setStartTime(LocalDate.now().atStartOfDay().plusHours(1L));
        drop1.setEndTime(LocalDate.now().atStartOfDay().plusHours(1L).plusMinutes(15));
        dropRepository.save(drop1);
        final Drop drop2 = DropDataGenerator.drop(2, route, spot);
        drop2.setStartTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4));
        drop2.setEndTime(LocalDate.now().atStartOfDay().plusDays(6).plusHours(4).plusMinutes(15));
        dropRepository.save(drop2);
        final Drop drop3 = DropDataGenerator.drop(3, route, spot);
        drop3.setStartTime(LocalDate.now().atStartOfDay().minusHours(26));
        drop3.setEndTime(LocalDate.now().atStartOfDay().minusHours(26).plusMinutes(15));
        dropRepository.save(drop3);
        final Drop drop4 = DropDataGenerator.drop(4, route, spot);
        drop4.setStartTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4));
        drop4.setEndTime(LocalDate.now().atStartOfDay().plusDays(7).plusHours(4).plusMinutes(15));
        dropRepository.save(drop4);

        final String url = String.format("/spots/%s", spot.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.drops", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.spot.name", Matchers.equalTo("Spot nr 1")));
    }

    @Test
    void givenValidRequestWhenFindSpotsByMembershipsThenFindNotBlocked() throws Exception {
        //given
        spot.setName("Spot nr 1");
        spotRepository.save(spot);
        final SpotMembership membership1 = SpotDataGenerator.membership(spot, customer);
        membership1.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership1);
        final Spot spot2 = spotRepository.save(SpotDataGenerator.spot(2, company));
        final SpotMembership membership2 = SpotDataGenerator.membership(spot2, customer);
        membership2.setMembershipStatus(SpotMembershipStatus.PENDING);
        spotMembershipRepository.save(membership2);
        final Spot spot3 = spotRepository.save(SpotDataGenerator.spot(3, company));
        final SpotMembership membership3 = SpotDataGenerator.membership(spot3, customer);
        membership3.setMembershipStatus(SpotMembershipStatus.BLOCKED);
        spotMembershipRepository.save(membership3);
        spotRepository.save(SpotDataGenerator.spot(4, company));

        final Account account2 = accountRepository.save(AccountDataGenerator.companyAccount(2));
        final Company company2 = companyRepository.save(CompanyDataGenerator.company(2, account2, country));
        final Spot spot4 = spotRepository.save(SpotDataGenerator.spot(4, company2));
        final SpotMembership membership4 = SpotDataGenerator.membership(spot4, customer);
        membership4.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership4);
        final CompanyCustomerRelationship companyCustomerRelationship = CompanyDataGenerator.companyCustomerRelationship(company2, customer);
        companyCustomerRelationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        companyCustomerRelationshipRepository.save(companyCustomerRelationship);

        final String url = "/spots/memberships";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(this.account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(2)));
    }
}