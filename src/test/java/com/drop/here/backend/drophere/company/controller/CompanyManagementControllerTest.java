package com.drop.here.backend.drophere.company.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private Account account;
    private Country country;

    @BeforeEach
    void prepare() {
        country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                .account(account).build());
    }

    @AfterEach
    void cleanUp() {
        privilegeRepository.deleteAll();
        companyRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
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
        privilege.setName(PrivilegeService.COMPANY_BASIC_MANAGEMENT_PRIVILEGE);
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
        final String url = "/management/companies";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.registered", Matchers.equalTo(false)))
                .andExpect(jsonPath("$.name", Matchers.emptyOrNullString()));
        ;
    }

    @Test
    void givenExistingCompanyWhenGetCompanyThenGet() throws Exception {
        //given
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
    void givenExistingCompanyNotOwnerWhenGetCompanyThenGet() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_FULL_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName(PrivilegeService.COMPANY_BASIC_MANAGEMENT_PRIVILEGE);
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
}