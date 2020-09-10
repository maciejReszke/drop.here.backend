package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountStatus;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.BaseLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ProfileLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.enums.ExternalAuthenticationProvider;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends IntegrationBaseClass {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ImageRepository imageRepository;

    @AfterEach
    void cleanUp() {
        customerRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void givenValidDataWhenLoginThenLogin() throws Exception {
        //given
        final String url = "/authentication";
        final String password = "password";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1)
                .toBuilder()
                .password(passwordEncoder.encode(password))
                .build());

        final String json = objectMapper.writeValueAsString(BaseLoginRequest.builder()
                .mail(account.getMail())
                .password(password)
                .build());

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").isNotEmpty())
                .andExpect(jsonPath("tokenValidUntil").isNotEmpty());
    }

    @Test
    void givenInvalidDataWhenLoginThen401() throws Exception {
        //given
        final String url = "/authentication";
        final String password = "password";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1)
                .toBuilder()
                .password(passwordEncoder.encode(password))
                .build());

        final String json = objectMapper.writeValueAsString(BaseLoginRequest.builder()
                .mail(account.getMail())
                .password(password + "Invalid")
                .build());

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenValidDataWhenLoginOnProfileThenLogin() throws Exception {
        //given
        final String url = "/authentication/profile";
        final String password = "password";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name("OWN_PROFILE_MANAGEMENT").account(account).build());
        final String token = jwtService.createToken(account).getToken();
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account)
                .toBuilder()
                .password(passwordEncoder.encode(password))
                .build());
        privilegeRepository.save(Privilege.builder().name("priv2").accountProfile(accountProfile).build());

        final String json = objectMapper.writeValueAsString(ProfileLoginRequest.builder()
                .profileUid(accountProfile.getProfileUid())
                .password(password)
                .build());

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").isNotEmpty())
                .andExpect(jsonPath("tokenValidUntil").isNotEmpty());
    }

    @Test
    void givenInvalidDataWhenLoginOnProfileThen401() throws Exception {
        //given
        final String url = "/authentication/profile";
        final String password = "password";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final String token = jwtService.createToken(account).getToken();
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account)
                .toBuilder()
                .password(password)
                .build());

        final String json = objectMapper.writeValueAsString(ProfileLoginRequest.builder()
                .profileUid(accountProfile.getProfileUid())
                .password(password + "invalid")
                .build());

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        //then
        perform
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenValidDataInvalidTokenWhenLoginOnProfileThen401() throws Exception {
        //given
        final String url = "/authentication/profile";
        final String password = "password";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final String token = jwtService.createToken(account).getToken();
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account)
                .toBuilder()
                .password(password)
                .build());
        final String json = objectMapper.writeValueAsString(ProfileLoginRequest.builder()
                .profileUid(accountProfile.getProfileUid())
                .password(password)
                .build());

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer a" + token));

        //then
        perform
                .andExpect(status().isUnauthorized());
    }


    @Test
    void givenValidTokenWithoutProfileWhenGetAuthenticationThenGet() throws Exception {
        //given
        final String url = "/authentication";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name("privilege").build());
        account.setPrivileges(List.of(privilege));
        final String token = jwtService.createToken(account).getToken();

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId", equalTo(account.getId().intValue())))
                .andExpect(jsonPath("$.accountType", equalTo("COMPANY")))
                .andExpect(jsonPath("$.roles", contains("privilege")))
                .andExpect(jsonPath("$.tokenValidUntil", Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.mail", equalTo(account.getMail())))
                .andExpect(jsonPath("$.hasProfile", equalTo(false)))
                .andExpect(jsonPath("$.loggedOnProfile", equalTo(false)))
                .andExpect(jsonPath("$.profileUid", emptyOrNullString()))
                .andExpect(jsonPath("$.profileFirstName", emptyOrNullString()))
                .andExpect(jsonPath("$.profileLastName", emptyOrNullString()))
                .andExpect(jsonPath("$.profileType", emptyOrNullString()))
                .andExpect(jsonPath("$.accountStatus", equalTo("ACTIVE")));
    }

    @Test
    void givenValidTokenWithProfileWhenGetAuthenticationThenGet() throws Exception {
        //given
        final String url = "/authentication";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1).toBuilder()
                .isAnyProfileRegistered(true)
                .build());
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name("privilege").build());
        account.setPrivileges(List.of(privilege));
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        final Privilege privilegeProfile = privilegeRepository.save(Privilege.builder().accountProfile(accountProfile).name("privilege2").build());
        accountProfile.setPrivileges(List.of(privilegeProfile));
        final String token = jwtService.createToken(account, accountProfile).getToken();

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId", equalTo(account.getId().intValue())))
                .andExpect(jsonPath("$.accountType", equalTo("COMPANY")))
                .andExpect(jsonPath("$.roles", contains("privilege", "privilege2")))
                .andExpect(jsonPath("$.tokenValidUntil", Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.mail", equalTo(account.getMail())))
                .andExpect(jsonPath("$.hasProfile", equalTo(true)))
                .andExpect(jsonPath("$.loggedOnProfile", equalTo(true)))
                .andExpect(jsonPath("$.profileUid", equalTo(accountProfile.getProfileUid())))
                .andExpect(jsonPath("$.profileFirstName", equalTo(accountProfile.getFirstName())))
                .andExpect(jsonPath("$.profileLastName", equalTo(accountProfile.getLastName())))
                .andExpect(jsonPath("$.profileType", equalTo(accountProfile.getProfileType().name())))
                .andExpect(jsonPath("$.accountStatus", equalTo("ACTIVE")));
    }

    @Test
    void givenNotActiveUserValidTokenWhenGetAuthenticationThen401() throws Exception {
        //given
        final String url = "/authentication";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1).toBuilder()
                .accountStatus(AccountStatus.INACTIVE).build());
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name("privilege").build());
        account.setPrivileges(List.of(privilege));
        final String token = jwtService.createToken(account).getToken();

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidTokenWhenGetAuthenticationThen401() throws Exception {
        //given
        final String url = "/authentication";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name("privilege").build());
        account.setPrivileges(List.of(privilege));
        final String token = jwtService.createToken(account).getToken();

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token + "Lala"));

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void givenNotBearerTokenWhenGetAuthenticationThen401() throws Exception {
        //given
        final String url = "/authentication";
        final Account account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Privilege privilege = privilegeRepository.save(Privilege.builder().account(account).name("privilege").build());
        account.setPrivileges(List.of(privilege));
        final String token = jwtService.createToken(account).getToken();

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, token));

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void givenLackOfTokenWhenGetAuthenticationThen401() throws Exception {
        //given
        final String url = "/authentication";

        //when
        final ResultActions result = mockMvc.perform(get(url));

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void givenValidRequestWithImageResponseAndNotExistingAccountAndCustomerWhenLoginWithAuthenticationProviderThenLogin() throws Exception {
        //given
        final String url = "/authentication/external";
        final String json = objectMapper.writeValueAsString(
                ExternalAuthenticationProviderLoginRequest.builder()
                        .code("code123")
                        .provider(ExternalAuthenticationProvider.FACEBOOK.name())
                        .redirectUri("http://localhost:997/redirectUrl")
                        .build()
        );

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        final String userToken = "userToken123";

        // Exchanging token
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/v8.0/oauth/access_token?client_id=clientTestId&client_secret=clientTestSecret&redirect_uri=http://localhost:997/redirectUrl&code=code123"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" + "  \"access_token\" : \"" + userToken + "\"}", MediaType.APPLICATION_JSON));

        //Fetching user data (with picture)
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/me?access_token=userToken123&fields=email,first_name,last_name,picture"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" +
                                "  \"email\": \"customerEmail@gmail.com\",\n" +
                                "  \"first_name\": \"CustomerName\",\n" +
                                "  \"last_name\": \"CustomerLastName\",\n" +
                                "  \"picture\": {\n" +
                                "    \"data\": {\n" +
                                "      \"height\": 50,\n" +
                                "      \"is_silhouette\": false,\n" +
                                "      \"url\": \"http://pictureUrl.pl\",\n" +
                                "      \"width\": 50\n" +
                                "    }\n" +
                                "  }\n" +
                                "}", MediaType.APPLICATION_JSON));

        //fetching image
        server.expect(MockRestRequestMatchers.requestTo("http://pictureUrl.pl"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("image".getBytes(), MediaType.IMAGE_JPEG));

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyOrNullString())));

        assertThat(accountRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(2);
        assertThat(customerRepository.findAll()).hasSize(1);
        assertThat(imageRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestWithoutImageResponseAndNotExistingAccountAndCustomerWhenLoginWithAuthenticationProviderThenLogin() throws Exception {
        //given
        final String url = "/authentication/external";
        final String json = objectMapper.writeValueAsString(
                ExternalAuthenticationProviderLoginRequest.builder()
                        .code("code123")
                        .provider(ExternalAuthenticationProvider.FACEBOOK.name())
                        .redirectUri("http://localhost:997/redirectUrl")
                        .build()
        );

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        final String userToken = "userToken123";

        // Exchanging token
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/v8.0/oauth/access_token?client_id=clientTestId&client_secret=clientTestSecret&redirect_uri=http://localhost:997/redirectUrl&code=code123"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" + "  \"access_token\" : \"" + userToken + "\"}", MediaType.APPLICATION_JSON));

        //Fetching user data (with picture)
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/me?access_token=userToken123&fields=email,first_name,last_name,picture"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" +
                                "  \"email\": \"customerEmail@gmail.com\",\n" +
                                "  \"first_name\": \"CustomerName\",\n" +
                                "  \"last_name\": \"CustomerLastName\"\n" +
                                "}", MediaType.APPLICATION_JSON));

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyOrNullString())));

        assertThat(accountRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(2);
        assertThat(customerRepository.findAll()).hasSize(1);
        assertThat(imageRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestExistingAccountWithoutCustomerWhenLoginWithAuthenticationProviderThenLogin() throws Exception {
        //given
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setMail("customerEmail@gmail.com");
        accountRepository.save(account);
        final String url = "/authentication/external";
        final String json = objectMapper.writeValueAsString(
                ExternalAuthenticationProviderLoginRequest.builder()
                        .code("code123")
                        .provider(ExternalAuthenticationProvider.FACEBOOK.name())
                        .redirectUri("http://localhost:997/redirectUrl")
                        .build()
        );

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        final String userToken = "userToken123";

        // Exchanging token
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/v8.0/oauth/access_token?client_id=clientTestId&client_secret=clientTestSecret&redirect_uri=http://localhost:997/redirectUrl&code=code123"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" + "  \"access_token\" : \"" + userToken + "\"}", MediaType.APPLICATION_JSON));

        //Fetching user data (with picture)
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/me?access_token=userToken123&fields=email,first_name,last_name,picture"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" +
                                "  \"email\": \"customerEmail@gmail.com\",\n" +
                                "  \"first_name\": \"CustomerName\",\n" +
                                "  \"last_name\": \"CustomerLastName\"\n" +
                                "}", MediaType.APPLICATION_JSON));

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyOrNullString())));

        assertThat(accountRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(1);
        assertThat(customerRepository.findAll()).hasSize(1);
        assertThat(imageRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestExistingAccountWithExistingCustomerWhenLoginWithAuthenticationProviderThenLogin() throws Exception {
        //given
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setMail("customerEmail@gmail.com");
        accountRepository.save(account);
        customerRepository.save(CustomerDataGenerator.customer(1, account));
        final String url = "/authentication/external";
        final String json = objectMapper.writeValueAsString(
                ExternalAuthenticationProviderLoginRequest.builder()
                        .code("code123")
                        .provider(ExternalAuthenticationProvider.FACEBOOK.name())
                        .redirectUri("http://localhost:997/redirectUrl")
                        .build()
        );

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        final String userToken = "userToken123";

        // Exchanging token
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/v8.0/oauth/access_token?client_id=clientTestId&client_secret=clientTestSecret&redirect_uri=http://localhost:997/redirectUrl&code=code123"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" + "  \"access_token\" : \"" + userToken + "\"}", MediaType.APPLICATION_JSON));

        //Fetching user data (with picture)
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/me?access_token=userToken123&fields=email,first_name,last_name,picture"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" +
                                "  \"email\": \"customerEmail@gmail.com\",\n" +
                                "  \"first_name\": \"CustomerName\",\n" +
                                "  \"last_name\": \"CustomerLastName\"\n" +
                                "}", MediaType.APPLICATION_JSON));

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.token", Matchers.not(Matchers.emptyOrNullString())));

        assertThat(accountRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).isEmpty();
        assertThat(customerRepository.findAll()).hasSize(1);
        assertThat(imageRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestCompanyAccountWhenLoginWithAuthenticationProviderThen401() throws Exception {
        //given
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setMail("customerEmail@gmail.com");
        account.setAccountType(AccountType.COMPANY);
        accountRepository.save(account);
        final String url = "/authentication/external";
        final String json = objectMapper.writeValueAsString(
                ExternalAuthenticationProviderLoginRequest.builder()
                        .code("code123")
                        .provider(ExternalAuthenticationProvider.FACEBOOK.name())
                        .redirectUri("http://localhost:997/redirectUrl")
                        .build()
        );

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        final String userToken = "userToken123";

        // Exchanging token
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/v8.0/oauth/access_token?client_id=clientTestId&client_secret=clientTestSecret&redirect_uri=http://localhost:997/redirectUrl&code=code123"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" + "  \"access_token\" : \"" + userToken + "\"}", MediaType.APPLICATION_JSON));

        //Fetching user data (with picture)
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/me?access_token=userToken123&fields=email,first_name,last_name,picture"))
                .andRespond(MockRestResponseCreators
                        .withSuccess("{\n" +
                                "  \"email\": \"customerEmail@gmail.com\",\n" +
                                "  \"first_name\": \"CustomerName\",\n" +
                                "  \"last_name\": \"CustomerLastName\"\n" +
                                "}", MediaType.APPLICATION_JSON));

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isUnauthorized());

        assertThat(accountRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).isEmpty();
        assertThat(customerRepository.findAll()).isEmpty();
        assertThat(imageRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidAccessTokenWhenLoginWithAuthenticationProviderThen401() throws Exception {
        //given
        final String url = "/authentication/external";
        final String json = objectMapper.writeValueAsString(
                ExternalAuthenticationProviderLoginRequest.builder()
                        .code("code123")
                        .provider(ExternalAuthenticationProvider.FACEBOOK.name())
                        .redirectUri("http://localhost:997/redirectUrl")
                        .build()
        );

        final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        final String userToken = "userToken123";

        // Exchanging token
        server.expect(MockRestRequestMatchers.requestTo("https://graph.facebook.com/v8.0/oauth/access_token?client_id=clientTestId&client_secret=clientTestSecret&redirect_uri=http://localhost:997/redirectUrl&code=code123"))
                .andRespond(MockRestResponseCreators.withBadRequest());

        //when
        final ResultActions perform = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isUnauthorized());

        assertThat(accountRepository.findAll()).isEmpty();
        assertThat(privilegeRepository.findAll()).isEmpty();
        assertThat(customerRepository.findAll()).isEmpty();
        assertThat(imageRepository.findAll()).isEmpty();
    }
}