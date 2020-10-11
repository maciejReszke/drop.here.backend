package com.drop.here.backend.drophere.company.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.dto.CompanyCustomerRelationshipManagementRequest;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.repository.CompanyCustomerRelationshipRepository;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyManagementControllerTest extends IntegrationBaseClass {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private SpotMembershipRepository spotMembershipRepository;

    @Autowired
    private CompanyCustomerRelationshipRepository companyCustomerRelationshipRepository;

    private Account account;
    private Country country;
    private Customer customer;

    @BeforeEach
    void prepare() {
        country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        final Account customerAccount = accountRepository.save(AccountDataGenerator.customerAccount(2));
        customer = customerRepository.save(CustomerDataGenerator.customer(1, customerAccount));
    }

    @AfterEach
    void cleanUp() {
        companyCustomerRelationshipRepository.deleteAll();
        spotMembershipRepository.deleteAll();
        spotRepository.deleteAll();
        privilegeRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void givenValidRequestNotExistingCompanyWhenUpdateCompanyThenUpdate() throws Exception {
        //given
        final String url = "/management/companies";
        final String json = objectMapper.writeValueAsString(CompanyDataGenerator.managementRequest(1)
                .toBuilder()
                .country(country.getName())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(companyRepository.findAll()).hasSize(1);
        assertThat(privilegeRepository.findAll()).hasSize(2);
    }

    @Test
    void givenValidRequestExistingCompanyWhenUpdateCompanyThenUpdate() throws Exception {
        //given
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final String url = "/management/companies";
        final CompanyManagementRequest request = CompanyDataGenerator.managementRequest(1)
                .toBuilder()
                .country(country.getName())
                .build();
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(companyRepository.findAll()).hasSize(1);
        assertThat(companyRepository.findAll().get(0).getName()).isEqualTo(request.getName());
        assertThat(privilegeRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenUpdateCompanyThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_FULL_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY);
        privilegeRepository.save(privilege);

        final String url = "/management/companies";
        final String json = objectMapper.writeValueAsString(CompanyDataGenerator.managementRequest(1)
                .toBuilder()
                .country(country.getName())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(companyRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestWhenUpdateCompanyThen422() throws Exception {
        //given
        final String url = "/management/companies";
        final String json = objectMapper.writeValueAsString(CompanyDataGenerator.managementRequest(1)
                .toBuilder()
                .country(country.getName())
                .visibilityStatus("ninja")
                .build());
        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        assertThat(companyRepository.findAll()).isEmpty();
    }

    @Test
    void givenNotExistingCompanyWhenGetCompanyThenGet() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_FULL_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY);
        privilegeRepository.save(privilege);
        final String url = "/management/companies";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.registered", Matchers.equalTo(false)))
                .andExpect(jsonPath("$.name", Matchers.emptyOrNullString()));
    }

    @Test
    void givenExistingCompanyWhenGetCompanyThenGet() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_FULL_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY);
        privilegeRepository.save(privilege);
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final String url = "/management/companies";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.registered", Matchers.equalTo(true)))
                .andExpect(jsonPath("$.name", Matchers.equalTo("companyName1")));
    }

    @Test
    void givenInvalidPrivilegeWhenGetCompanyThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_FULL_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE);
        privilegeRepository.save(privilege);

        final String url = "/management/companies";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestNotExistingImageWhenUpdateImageThenUpdate() throws Exception {
        //given
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final String url = "/management/companies/images";
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());

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
        final Company company = CompanyDataGenerator.company(1, account, country);
        company.setImage(image);
        companyRepository.save(company);
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        final String url = "/management/companies/images";
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
    void givenValidRequestLackOfResourceManagementPrivilegesWhenUpdateImageThen403() throws Exception {
        //given
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final String url = "/management/companies/images";
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

    @Test
    void givenValidRequestInvalidPrivilegesWhenUpdateImageThen403() throws Exception {
        //given
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_FULL_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY);
        privilegeRepository.save(privilege);
        final String url = "/management/companies/images";
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

    @Test
    void givenValidRequestCompaniesCustomerBySpotMembershipWhenUpdateCustomerRelationshipThenUpdate() throws Exception {
        //given
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());

        final String url = String.format("/management/companies/customers/%s", customer.getId());
        final String json = objectMapper.writeValueAsString(CompanyCustomerRelationshipManagementRequest.builder().block(true).build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(companyCustomerRelationshipRepository.findAll()).hasSize(1);
        assertThat(companyCustomerRelationshipRepository.findAll().get(0).getRelationshipStatus())
                .isEqualTo(CompanyCustomerRelationshipStatus.BLOCKED);
    }

    @Test
    void givenValidRequestCompaniesCustomerByCompanyCustomerRelationshipWhenUpdateCustomerRelationshipThenUpdate() throws Exception {
        //given
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        companyCustomerRelationshipRepository.save(CompanyDataGenerator.companyCustomerRelationship(company, customer));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());

        final String url = String.format("/management/companies/customers/%s", customer.getId());
        final String json = objectMapper.writeValueAsString(CompanyCustomerRelationshipManagementRequest.builder().block(false).build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(companyCustomerRelationshipRepository.findAll()).hasSize(1);
        assertThat(companyCustomerRelationshipRepository.findAll().get(0).getRelationshipStatus())
                .isEqualTo(CompanyCustomerRelationshipStatus.ACTIVE);
    }

    @Test
    void givenValidRequestWithoutRelationshipWhenUpdateCustomerRelationshipThen403() throws Exception {
        //given
        companyRepository.save(CompanyDataGenerator.company(1, account, country));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());

        final String url = String.format("/management/companies/customers/%s", customer.getId());
        final String json = objectMapper.writeValueAsString(CompanyCustomerRelationshipManagementRequest.builder().block(false).build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(companyCustomerRelationshipRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestInvalidPrivilegeWhenUpdateCustomerRelationshipThen403() throws Exception {
        //given
        companyRepository.save(CompanyDataGenerator.company(1, account, country));

        final String url = String.format("/management/companies/customers/%s", customer.getId());
        final String json = objectMapper.writeValueAsString(CompanyCustomerRelationshipManagementRequest.builder().block(true).build());


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(companyCustomerRelationshipRepository.findAll()).isEmpty();
    }

    @Test
    void givenWithoutParamRequestWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(4)));
    }

    @Test
    void givenWithNameParamRequestWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "Krzy")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenWithNameParamRequestLastNameFirstNameConcatWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "Krzywousty Mich")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenWithNameParamRequestFirstNameLastNameConcatWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "Michal Krzy")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenWithBlockedTrueParamRequestWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "")
                .param("blocked", "true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenWithBlockedFalseParamRequestWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "")
                .param("blocked", "false")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(3)));
    }

    @Test
    void givenWithBlockedFalseParamAndNameRequestWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "Michal")
                .param("blocked", "false")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenWithBlockedTrueParamAndNameRequestWhenFindCustomersThenFindCustomers() throws Exception {
        //given
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                .account(account).build());
        prepareFindingCompaniesCustomerData();


        final String url = "/management/companies/customers";

        //when
        final ResultActions perform = mockMvc.perform(get(url)
                .param("customerName", "Michal")
                .param("blocked", "true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(0)));
    }

    private void prepareFindingCompaniesCustomerData() {
        final Company company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        companyCustomerRelationshipRepository.save(CompanyDataGenerator.companyCustomerRelationship(company, customer));

        final Account customerAccount2 = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer customer2 = CustomerDataGenerator.customer(2, customerAccount2);
        customer2.setFirstName("Michal");
        customer2.setLastName("Krzywousty");
        customerRepository.save(customer2);
        final CompanyCustomerRelationship relationship1 = CompanyDataGenerator.companyCustomerRelationship(company, customer2);
        relationship1.setRelationshipStatus(CompanyCustomerRelationshipStatus.ACTIVE);
        companyCustomerRelationshipRepository.save(relationship1);

        final Account customerAccount3 = accountRepository.save(AccountDataGenerator.customerAccount(4));
        final Customer customer3 = customerRepository.save(CustomerDataGenerator.customer(3, customerAccount3));
        final Account differentCompanyAccount = accountRepository.save(AccountDataGenerator.customerAccount(5));
        final Company differentCompany = companyRepository.save(CompanyDataGenerator.company(2, differentCompanyAccount, country));
        companyCustomerRelationshipRepository.save(CompanyDataGenerator.companyCustomerRelationship(differentCompany, customer3));

        final Account customerAccount4 = accountRepository.save(AccountDataGenerator.customerAccount(6));
        final Customer customer4 = CustomerDataGenerator.customer(4, customerAccount4);
        customer4.setFirstName("Krzysztof");
        customer4.setLastName("Tlusty");
        customerRepository.save(customer4);
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer4));

        final Account customerAccount5 = accountRepository.save(AccountDataGenerator.customerAccount(7));
        final Customer customer5 = CustomerDataGenerator.customer(5, customerAccount5);
        customer5.setFirstName("Cham");
        customer5.setLastName("Zwykly");
        customerRepository.save(customer5);
        final Spot spot2 = spotRepository.save(SpotDataGenerator.spot(2, company));
        final SpotMembership membership = SpotDataGenerator.membership(spot2, customer5);
        spotMembershipRepository.save(membership);
        final CompanyCustomerRelationship relationship2 = CompanyDataGenerator.companyCustomerRelationship(company, customer5);
        relationship2.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        companyCustomerRelationshipRepository.save(relationship2);
    }
}