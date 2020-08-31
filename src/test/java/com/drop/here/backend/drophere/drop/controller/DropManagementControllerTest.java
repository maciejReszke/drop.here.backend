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
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
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
        dropRepository.deleteAll();
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
        request.setLocationDropType(DropLocationType.GEOLOCATION.name() + "i");
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
        request.setLocationDropType(DropLocationType.GEOLOCATION.name() + "i");
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
    void givenValidRequestOwnCompanyOperationWhenDeleteDropThenDelete() throws Exception {
        //given
        final Drop drop = dropRepository.save(DropDataGenerator.drop(1, company));
        final String url = String.format("/companies/%s/drops/%s", company.getUid(), drop.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(dropRepository.findAll()).isEmpty();
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


}