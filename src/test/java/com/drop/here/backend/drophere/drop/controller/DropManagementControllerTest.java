package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.DropStatusChange;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationJobRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE;
import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class DropManagementControllerTest extends IntegrationBaseClass {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

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
    private DropRepository dropRepository;

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private NotificationJobRepository notificationJobRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SpotMembershipRepository spotMembershipRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Company company;
    private Account account;
    private Spot spot;
    private AccountProfile seller;
    private AccountProfile owner;
    private Route route;
    private Customer customer;

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        seller = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account).toBuilder()
                .profileType(AccountProfileType.SUBPROFILE).build());
        owner = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(2, account).toBuilder()
                .profileType(AccountProfileType.MAIN).build());
        route = RouteDataGenerator.route(1, company);
        route.setProfile(seller);
        routeRepository.save(route);
        final Account customerAccount = accountRepository.save(AccountDataGenerator.customerAccount(2));
        customer = customerRepository.save(CustomerDataGenerator.customer(1, customerAccount));
        notificationTokenRepository.save(NotificationToken.builder()
                .tokenType(NotificationTokenType.CUSTOMER)
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE)
                .ownerCustomer(customer)
                .token("token123")
                .build());
        privilegeRepository.save(Privilege.builder().name(LOGGED_ON_ANY_PROFILE_COMPANY).accountProfile(seller).build());
        privilegeRepository.save(Privilege.builder().name(LOGGED_ON_ANY_PROFILE_COMPANY).accountProfile(owner).build());
    }


    @AfterEach
    void cleanUp() {
        spotMembershipRepository.deleteAll();
        notificationJobRepository.deleteAll();
        notificationRepository.deleteAll();
        notificationTokenRepository.deleteAll();
        routeRepository.deleteAll();
        spotRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        customerRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenValidRequestSellerCustomerSubscribedToLiveWhenUpdateDropThenUpdate() throws Exception {
        //given
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.PREPARED);
        dropRepository.save(drop);
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer)
                .toBuilder()
                .receiveLiveNotifications(true)
                .build());

        final String url = String.format("/management/companies/drops/%s", drop.getUid());
        final String json = objectMapper.writeValueAsString(DropManagementRequest.builder()
                .newStatus(DropStatusChange.LIVE)
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, seller).getToken()));

        //then
        result.andExpect(status().isOk());
        assertThat(dropRepository.findAll().get(0).getStatus()).isEqualTo(DropStatus.LIVE);
        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationJobRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestCompanyOwnerCustomerNotSubscribedToLiveWhenUpdateDropThenUpdate() throws Exception {
        //given
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.PREPARED);
        dropRepository.save(drop);
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer)
                .toBuilder()
                .receiveLiveNotifications(false)
                .build());

        final String url = String.format("/management/companies/drops/%s", drop.getUid());
        final String json = objectMapper.writeValueAsString(DropManagementRequest.builder()
                .newStatus(DropStatusChange.LIVE)
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, seller).getToken()));

        //then
        result.andExpect(status().isOk());
        assertThat(dropRepository.findAll().get(0).getStatus()).isEqualTo(DropStatus.LIVE);
        assertThat(notificationRepository.findAll()).isEmpty();
        assertThat(notificationJobRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestCompanyOwnerCustomerWithoutTokenWhenUpdateDropThenUpdate() throws Exception {
        //given
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.PREPARED);
        dropRepository.save(drop);
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer)
                .toBuilder()
                .receiveLiveNotifications(true)
                .build());
        notificationTokenRepository.deleteAll();

        final String url = String.format("/management/companies/drops/%s", drop.getUid());
        final String json = objectMapper.writeValueAsString(DropManagementRequest.builder()
                .newStatus(DropStatusChange.LIVE)
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, seller).getToken()));

        //then
        result.andExpect(status().isOk());
        assertThat(dropRepository.findAll().get(0).getStatus()).isEqualTo(DropStatus.LIVE);
        assertThat(notificationRepository.findAll()).isEmpty();
        assertThat(notificationJobRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestNotSellerNorOwnerWhenUpdateDropThen403() throws Exception {
        //given
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        drop.setStatus(DropStatus.PREPARED);
        dropRepository.save(drop);
        route.setProfile(null);
        routeRepository.save(route);

        final String url = String.format("/management/companies/drops/%s", drop.getUid());
        final String json = objectMapper.writeValueAsString(DropManagementRequest.builder()
                .newStatus(DropStatusChange.LIVE)
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, seller).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(dropRepository.findAll().get(0).getStatus()).isEqualTo(DropStatus.PREPARED);
        assertThat(notificationRepository.findAll()).isEmpty();
        assertThat(notificationJobRepository.findAll()).isEmpty();
    }

}