package com.drop.here.backend.drophere.schedule_template.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateProductRequest;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplateProduct;
import com.drop.here.backend.drophere.schedule_template.repository.ScheduleTemplateRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductUnitDataGenerator;
import com.drop.here.backend.drophere.test_data.ScheduleTemplateDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScheduleTemplateControllerTest extends IntegrationBaseClass {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;

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
    private ProductCustomizationWrapperRepository productCustomizationWrapperRepository;
    private Company company;
    private Account account;
    private Product product;

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        final ProductUnit productUnit = productUnitRepository.save(ProductUnitDataGenerator.productUnit(1));
        product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
    }


    @AfterEach
    void cleanUp() {
        scheduleTemplateRepository.deleteAll();
        productCustomizationWrapperRepository.deleteAll();
        productRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
        productUnitRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenCreateScheduleTemplateThenCreate() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId())
                        .limitedAmount(true)
                        .amount(14)
                        .build()))
                .name("name123")
                .build();
        final String url = String.format("/companies/%s/schedule_templates", company.getUid());
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(scheduleTemplateRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenCreateScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId())
                        .limitedAmount(true)
                        .amount(14)
                        .build()))
                .name("name123")
                .build();
        final String url = String.format("/companies/%s/schedule_templates", company.getUid() + "kek");
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(scheduleTemplateRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegesWhenCreateScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId())
                        .limitedAmount(true)
                        .amount(14)
                        .build()))
                .name("name123")
                .build();
        final String url = String.format("/companies/%s/schedule_templates", company.getUid());
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);
        final Privilege privilege = privilegeRepository.findAll().get(0);
        privilege.setName("kaka");
        privilegeRepository.save(privilege);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(scheduleTemplateRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestNotFoundProductOwnCompanyOperationWhenCreateScheduleTemplateThen404() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId() + 15)
                        .limitedAmount(true)
                        .build()))
                .name("name123")
                .build();
        final String url = String.format("/companies/%s/schedule_templates", company.getUid());
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(scheduleTemplateRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateScheduleTemplateThenUpdate() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId())
                        .limitedAmount(true)
                        .amount(14)
                        .build()))
                .name("name123")
                .build();
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId());
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(scheduleTemplateRepository.findAll()).hasSize(1);
        assertThat(scheduleTemplateRepository.findAll().get(0).getName()).isEqualTo("name123");
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId())
                        .limitedAmount(true)
                        .amount(14)
                        .build()))
                .name("name123")
                .build();
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid() + "kek", scheduleTemplate.getId());
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(scheduleTemplateRepository.findAll()).hasSize(1);
        assertThat(scheduleTemplateRepository.findAll().get(0).getName()).isNotEqualTo("name123");
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegesWhenUpdateScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId())
                        .limitedAmount(true)
                        .amount(14)
                        .build()))
                .name("name123")
                .build();
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId());
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);
        final Privilege privilege = privilegeRepository.findAll().get(0);
        privilege.setName("kaka");
        privilegeRepository.save(privilege);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(scheduleTemplateRepository.findAll()).hasSize(1);
        assertThat(scheduleTemplateRepository.findAll().get(0).getName()).isNotEqualTo("name123");
    }

    @Test
    void givenInvalidRequestProductNotFoundOwnCompanyOperationWhenUpdateScheduleTemplateThen404() throws Exception {
        //given
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateManagementRequest.builder()
                .scheduleTemplateProducts(List.of(ScheduleTemplateProductRequest.builder()
                        .price(BigDecimal.valueOf(55.33d))
                        .productId(product.getId() + 15)
                        .limitedAmount(true)
                        .build()))
                .name("name123")
                .build();
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId());
        final String json = objectMapper.writeValueAsString(scheduleTemplateManagementRequest);

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(scheduleTemplateRepository.findAll()).hasSize(1);
        assertThat(scheduleTemplateRepository.findAll().get(0).getName()).isNotEqualTo("name123");
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenDeleteScheduleTemplateThenDelete() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(scheduleTemplateRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenDeleteScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid() + "kek", scheduleTemplate.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(scheduleTemplateRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegesWhenDeleteScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId());
        final Privilege privilege = privilegeRepository.findAll().get(0);
        privilege.setName("kaka");
        privilegeRepository.save(privilege);

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(scheduleTemplateRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenInvalidRequestProductNotFoundOwnCompanyOperationWhenDeleteScheduleTemplateThen404() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.save(ScheduleTemplateDataGenerator.scheduleTemplate(1, company));
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId() + 15);

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(scheduleTemplateRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindScheduleTemplateThenFind() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(ScheduleTemplateProduct.builder()
                .product(product)
                .orderNum(12)
                .limitedAmount(true)
                .amount(15)
                .scheduleTemplate(scheduleTemplate)
                .price(BigDecimal.valueOf(15))
                .build()));
        scheduleTemplateRepository.save(scheduleTemplate);
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()));
    }

    @Test
    void givenNotOwnCompanyOperationWhenFindScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(ScheduleTemplateProduct.builder()
                .product(product)
                .orderNum(12)
                .limitedAmount(true)
                .amount(15)
                .scheduleTemplate(scheduleTemplate)
                .price(BigDecimal.valueOf(15))
                .build()));
        scheduleTemplateRepository.save(scheduleTemplate);
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid() + "kek", scheduleTemplate.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidPrivilegesWhenFindScheduleTemplateThenForbidden() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(ScheduleTemplateProduct.builder()
                .product(product)
                .orderNum(12)
                .limitedAmount(true)
                .amount(15)
                .scheduleTemplate(scheduleTemplate)
                .price(BigDecimal.valueOf(15))
                .build()));
        scheduleTemplateRepository.save(scheduleTemplate);
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId());

        final Privilege privilege = privilegeRepository.findAll().get(0);
        privilege.setName("kaka");
        privilegeRepository.save(privilege);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenNotExistingScheduleTemplateOwnCompanyOperationWhenFindScheduleTemplateThen404() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(ScheduleTemplateProduct.builder()
                .product(product)
                .orderNum(12)
                .limitedAmount(true)
                .amount(15)
                .scheduleTemplate(scheduleTemplate)
                .price(BigDecimal.valueOf(15))
                .build()));
        scheduleTemplateRepository.save(scheduleTemplate);
        final String url = String.format("/companies/%s/schedule_templates/%s", company.getUid(), scheduleTemplate.getId() + 15);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindScheduleTemplatesThenFind() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(ScheduleTemplateProduct.builder()
                .product(product)
                .orderNum(12)
                .limitedAmount(true)
                .amount(15)
                .scheduleTemplate(scheduleTemplate)
                .price(BigDecimal.valueOf(15))
                .build()));
        scheduleTemplateRepository.save(scheduleTemplate);
        final String url = String.format("/companies/%s/schedule_templates", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenNotOwnCompanyOperationWhenFindScheduleTemplatesThenForbidden() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(ScheduleTemplateProduct.builder()
                .product(product)
                .orderNum(12)
                .limitedAmount(true)
                .amount(15)
                .scheduleTemplate(scheduleTemplate)
                .price(BigDecimal.valueOf(15))
                .build()));
        scheduleTemplateRepository.save(scheduleTemplate);
        final String url = String.format("/companies/%s/schedule_templates", company.getUid() + "kek");

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidPrivilegesWhenFindScheduleTemplatesThenForbidden() throws Exception {
        //given
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(ScheduleTemplateProduct.builder()
                .product(product)
                .orderNum(12)
                .limitedAmount(true)
                .amount(15)
                .scheduleTemplate(scheduleTemplate)
                .price(BigDecimal.valueOf(15))
                .build()));
        scheduleTemplateRepository.save(scheduleTemplate);
        final String url = String.format("/companies/%s/schedule_templates", company.getUid());

        final Privilege privilege = privilegeRepository.findAll().get(0);
        privilege.setName("kaka");
        privilegeRepository.save(privilege);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }
}