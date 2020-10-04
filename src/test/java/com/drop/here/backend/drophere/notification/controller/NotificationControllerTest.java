package com.drop.here.backend.drophere.notification.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
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
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.NotificationDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends IntegrationBaseClass {

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
    private NotificationRepository notificationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationTokenRepository tokenRepository;

    private Country country;

    @BeforeEach
    void prepare() {
        country = countryRepository.save(CountryDataGenerator.poland());
    }

    @AfterEach
    void cleanUp() {
        tokenRepository.deleteAll();
        notificationRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenAccountProfileWhenFindNotificationsThenFind() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).accountProfile(accountProfile).build());
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        notificationRepository.save(NotificationDataGenerator.accountProfileNotification(1, accountProfile));
        notificationRepository.save(NotificationDataGenerator.companyNotification(2, company));

        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, accountProfile).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenCompanyAccountWhenFindNotificationsThenFind() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        notificationRepository.save(NotificationDataGenerator.companyNotification(1, company));

        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenCompanyAccountWithNotificationReadTypeExistingNotificationWhenFindNotificationsThenFind() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Notification notification = NotificationDataGenerator.companyNotification(1, company);
        notification.setReadStatus(NotificationReadStatus.UNREAD);
        notificationRepository.save(notification);

        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("readStatus", NotificationReadStatus.UNREAD.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenCompanyAccountWithNotificationReadTypeNotExistingNotificationWhenFindNotificationsThenEmpty() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Notification notification = NotificationDataGenerator.companyNotification(1, company);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);

        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("readStatus", NotificationReadStatus.UNREAD.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.empty()));
    }

    @Test
    void givenCompanyAccountInvalidPrivilegeWhenFindNotificationsThenForbidden() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY).account(account).build());
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        notificationRepository.save(NotificationDataGenerator.companyNotification(1, company));

        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenCustomerAccountWhenFindNotificationsThenFind() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.UNREAD);
        notificationRepository.save(notification);
        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenCustomerAccountWithNotificationReadTypeExistingNotificationWhenFindNotificationsThenFind() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.UNREAD);
        notificationRepository.save(notification);

        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("readStatus", NotificationReadStatus.UNREAD.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenCustomerAccountWithNotificationReadTypeNotExistingNotificationWhenFindNotificationsThenEmpty() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);

        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("readStatus", NotificationReadStatus.UNREAD.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.empty()));
    }

    @Test
    void givenCustomerAccountInvalidPrivilegesWhenFindNotificationsThenForbidden() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.UNREAD);
        notificationRepository.save(notification);
        final String url = "/notifications";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCustomerNotificationWhenUpdateNotificationThenUpdate() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);
        final String url = String.format("/notifications/%s", notification.getId());
        final NotificationManagementRequest request = NotificationManagementRequest.builder()
                .readStatus(NotificationReadStatus.UNREAD.name()).build();
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll().stream().findFirst().orElseThrow().getReadStatus())
                .isEqualTo(NotificationReadStatus.UNREAD);
    }

    @Test
    void givenValidRequestOwnCompanyNotificationWhenUpdateNotificationThenUpdate() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        final Company customer = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Notification notification = NotificationDataGenerator.companyNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);
        final String url = String.format("/notifications/%s", notification.getId());
        final NotificationManagementRequest request = NotificationManagementRequest.builder()
                .readStatus(NotificationReadStatus.UNREAD.name()).build();
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll().stream().findFirst().orElseThrow().getReadStatus())
                .isEqualTo(NotificationReadStatus.UNREAD);
    }

    @Test
    void givenValidRequestAccountProfileNotificationWhenUpdateNotificationThenUpdate() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1).toBuilder()
                .isAnyProfileRegistered(true)
                .build());
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).accountProfile(accountProfile).build());
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Notification notification = NotificationDataGenerator.accountProfileNotification(1, accountProfile);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);
        final String url = String.format("/notifications/%s", notification.getId());
        final NotificationManagementRequest request = NotificationManagementRequest.builder()
                .readStatus(NotificationReadStatus.UNREAD.name()).build();
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, accountProfile).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll().stream().findFirst().orElseThrow().getReadStatus())
                .isEqualTo(NotificationReadStatus.UNREAD);
    }


    @Test
    void givenValidRequestNotOwnNotificationWhenUpdateNotificationThenNotFound() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Account account2 = accountRepository.save(AccountDataGenerator.customerAccount(2));
        final Customer customer2 = customerRepository.save(CustomerDataGenerator.customer(2, account2));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer2);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);
        final String url = String.format("/notifications/%s", notification.getId());
        final NotificationManagementRequest request = NotificationManagementRequest.builder()
                .readStatus(NotificationReadStatus.UNREAD.name()).build();
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll().stream().findFirst().orElseThrow().getReadStatus())
                .isEqualTo(NotificationReadStatus.READ);
    }

    @Test
    void givenValidRequestOwnNotificationInvalidPrivilegeWhenUpdateNotificationThen403() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);
        final String url = String.format("/notifications/%s", notification.getId());
        final NotificationManagementRequest request = NotificationManagementRequest.builder()
                .readStatus(NotificationReadStatus.UNREAD.name()).build();
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll().stream().findFirst().orElseThrow().getReadStatus())
                .isEqualTo(NotificationReadStatus.READ);
    }

    @Test
    void givenInvalidRequestWhenUpdateNotificationThen422() throws Exception {
        //given
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        notification.setReadStatus(NotificationReadStatus.READ);
        notificationRepository.save(notification);
        final String url = String.format("/notifications/%s", notification.getId());
        final NotificationManagementRequest request = NotificationManagementRequest.builder()
                .readStatus(NotificationReadStatus.UNREAD.name() + "kaka").build();
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(422));

        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll().stream().findFirst().orElseThrow().getReadStatus())
                .isEqualTo(NotificationReadStatus.READ);
    }

    @Test
    void givenCustomerAuthenticationNotExistingWhenUpdateTokenThenCreate() throws Exception {
        //given
        final String url = "/notifications/tokens";
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest
                .builder()
                .token("token123")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name())
                .build();
        final String json = objectMapper.writeValueAsString(notificationTokenManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(tokenRepository.findAll()).hasSize(1);
        assertThat(tokenRepository.findAll().get(0).getToken()).isEqualTo("token123");
    }

    @Test
    void givenCustomerAuthenticationExistingWhenUpdateTokenThenUpdate() throws Exception {
        //given
        final String url = "/notifications/tokens";
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        final Customer customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        tokenRepository.save(NotificationToken.builder().token("queue").broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE).ownerCustomer(customer).tokenType(NotificationTokenType.CUSTOMER).build());
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest
                .builder()
                .token("token123")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name())
                .build();
        final String json = objectMapper.writeValueAsString(notificationTokenManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(tokenRepository.findAll()).hasSize(1);
        assertThat(tokenRepository.findAll().get(0).getToken()).isEqualTo("token123");
    }

    @Test
    void givenProfileAuthenticationNotExistingWhenUpdateTokenThenCreate() throws Exception {
        //given
        final String url = "/notifications/tokens";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).accountProfile(accountProfile).build());
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest
                .builder()
                .token("token123")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name())
                .build();
        final String json = objectMapper.writeValueAsString(notificationTokenManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account,accountProfile).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(tokenRepository.findAll()).hasSize(1);
        assertThat(tokenRepository.findAll().get(0).getToken()).isEqualTo("token123");
    }

    @Test
    void givenProfileAuthenticationExistingWhenUpdateTokenThenUpdate() throws Exception {
        //given
        final String url = "/notifications/tokens";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).accountProfile(accountProfile).build());
        tokenRepository.save(NotificationToken.builder().token("queue").broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE).ownerAccountProfile(accountProfile).tokenType(NotificationTokenType.PROFILE).build());
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest
                .builder()
                .token("token123")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name())
                .build();
        final String json = objectMapper.writeValueAsString(notificationTokenManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account, accountProfile).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(tokenRepository.findAll()).hasSize(1);
        assertThat(tokenRepository.findAll().get(0).getToken()).isEqualTo("token123");
    }

    @Test
    void givenInvalidPrivilegesWhenUpdateTokenThen403() throws Exception {
        //given
        final String url = "/notifications/tokens";
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(account).build());
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest
                .builder()
                .token("token123")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name())
                .build();
        final String json = objectMapper.writeValueAsString(notificationTokenManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(tokenRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestWhenUpdateTokenThen400() throws Exception {
        //given
        final String url = "/notifications/tokens";
        final Account account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest
                .builder()
                .token("token123")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name() + "kaka")
                .build();
        final String json = objectMapper.writeValueAsString(notificationTokenManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is4xxClientError());

        assertThat(tokenRepository.findAll()).isEmpty();
    }

}