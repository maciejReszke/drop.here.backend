package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends IntegrationBaseClass {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;

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
    private ProductUnit productUnit;

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        productUnit = productUnitRepository.save(ProductUnitDataGenerator.productUnit(1));
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
    void givenValidRequestOwnCompanyOperationWhenFindProductsThenFind() throws Exception {
        //given
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hot dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        final Product preSaved3 = ProductDataGenerator.product(3, productUnit, company);
        preSaved3.setName("Arsenic");
        preSaved3.setCategory("poison");
        productRepository.save(preSaved3);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        final String url = String.format("/companies/%s/products", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", hasSize(3)))
                .andExpect(jsonPath("$.content[0].id", equalTo(preSaved1.getId().intValue())))
                .andExpect(jsonPath("$.content[1].id", equalTo(preSaved2.getId().intValue())))
                .andExpect(jsonPath("$.content[2].id", equalTo(preSaved3.getId().intValue())));
    }

    @Test
    void givenValidRequestOwnCompanyOperationOneCategoryWhenFindProductsThenFind() throws Exception {
        //given
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hot dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        final Product preSaved3 = ProductDataGenerator.product(3, productUnit, company);
        preSaved3.setName("Arsenic");
        preSaved3.setCategory("poison");
        productRepository.save(preSaved3);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        final String url = String.format("/companies/%s/products", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("category", preSaved2.getCategory())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", equalTo(preSaved2.getId().intValue())));
    }

    @Test
    void givenValidRequestOwnCompanyOperationTwoCategoriesWhenFindProductsThenFind() throws Exception {
        //given
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hot dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        final Product preSaved3 = ProductDataGenerator.product(3, productUnit, company);
        preSaved3.setName("Arsenic");
        preSaved3.setCategory("poison");
        productRepository.save(preSaved3);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        final String url = String.format("/companies/%s/products", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("category", preSaved2.getCategory(), preSaved1.getCategory())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", equalTo(preSaved1.getId().intValue())))
                .andExpect(jsonPath("$.content[1].id", equalTo(preSaved2.getId().intValue())));
    }

    @Test
    void givenValidRequestOwnCompanyOperationNameSubstringWhenFindProductsThenFind() throws Exception {
        //given
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hoft dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        final Product preSaved3 = ProductDataGenerator.product(3, productUnit, company);
        preSaved3.setName("Arsenic");
        preSaved3.setCategory("poison");
        productRepository.save(preSaved3);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        final String url = String.format("/companies/%s/products", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "of")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", equalTo(preSaved1.getId().intValue())))
                .andExpect(jsonPath("$.content[1].id", equalTo(preSaved2.getId().intValue())));
    }

    @Test
    void givenValidRequestOwnCompanyOperationNameEqualWhenFindProductsThenFind() throws Exception {
        //given
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hot dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        final Product preSaved3 = ProductDataGenerator.product(3, productUnit, company);
        preSaved3.setName("Arsenic");
        preSaved3.setCategory("poison");
        productRepository.save(preSaved3);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        final String url = String.format("/companies/%s/products", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "Hot dog")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", equalTo(preSaved1.getId().intValue())));
    }

    @Test
    void givenValidRequestOwnCompanyOperationNameSubstringAndCategoryWhenFindProductsThenFind() throws Exception {
        //given
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hoft dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        final Product preSaved3 = ProductDataGenerator.product(3, productUnit, company);
        preSaved3.setName("Arsenic");
        preSaved3.setCategory("poison");
        productRepository.save(preSaved3);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        final String url = String.format("/companies/%s/products", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("name", "of")
                .param("category", preSaved1.getCategory(), preSaved3.getCategory())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", equalTo(preSaved1.getId().intValue())));
    }


    @Test
    void givenValidRequestNotOwnCompanyOperationCompanyNotVisibleWhenGetProductThen403() throws Exception {
        //given
        company.setAccount(accountRepository.save(AccountDataGenerator.customerAccount(2)));
        final String url = String.format("/companies/%s/products", company.getUid());
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hot dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        company.setVisibilityStatus(CompanyVisibilityStatus.HIDDEN);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationCompanyVisibleWhenGetProductThenGet() throws Exception {
        //given
        account.setCompany(null);
        accountRepository.save(account);
        final String url = String.format("/companies/%s/products", company.getUid());
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hot dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        final Product preSaved3 = ProductDataGenerator.product(3, productUnit, company);
        preSaved3.setName("Arsenic");
        preSaved3.setCategory("poison");
        productRepository.save(preSaved3);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", hasSize(3)))
                .andExpect(jsonPath("$.content[0].id", equalTo(preSaved1.getId().intValue())))
                .andExpect(jsonPath("$.content[1].id", equalTo(preSaved2.getId().intValue())))
                .andExpect(jsonPath("$.content[2].id", equalTo(preSaved3.getId().intValue())));
    }

    @Test
    void givenValidRequestOwnCompanyOperationLackOfPrivilegeWhenGetProductThen401() throws Exception {
        //given
        privilegeRepository.deleteAll();

        final String url = String.format("/companies/%s/products", company.getUid());
        final Product preSaved1 = ProductDataGenerator.product(1, productUnit, company);
        preSaved1.setName("Hot dog");
        preSaved1.setCategory("food");
        productRepository.save(preSaved1);
        final Product preSaved2 = ProductDataGenerator.product(2, productUnit, company);
        preSaved2.setName("Coffee");
        preSaved2.setCategory("drink");
        productRepository.save(preSaved2);
        company.setVisibilityStatus(CompanyVisibilityStatus.VISIBLE);
        companyRepository.save(company);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenCreateProductThenCreate() throws Exception {
        //given
        final String url = String.format("/companies/%s/products", company.getUid());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .unit(productUnit.getName())
                .build());

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenCreateProductThen403() throws Exception {
        //given
        final String url = String.format("/companies/%s/products", company.getUid() + "i");
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .unit(productUnit.getName())
                .build());

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenCreateProductThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final String url = String.format("/companies/%s/products", company.getUid() + "i");
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .unit(productUnit.getName())
                .build());

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenCreateProductThen422() throws Exception {
        //given
        final String url = String.format("/companies/%s/products", company.getUid());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .unit(productUnit.getName())
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE + "ttt")
                .build());

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateProductThenUpdate() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s", company.getUid(), product.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .name("newName")
                .unit(productUnit.getName())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(productRepository.findAll()).hasSize(1);
        assertThat(productRepository.findAll().get(0).getName()).isEqualTo("newName");
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateProductThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s", company.getUid() + "ii", product.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .name("newName")
                .unit(productUnit.getName())
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenUpdateProductThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s", company.getUid(), product.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .unit(productUnit.getName())
                .name("newName")
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenUpdateProductThen422() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s", company.getUid(), product.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.managementRequest(1)
                .toBuilder()
                .unit(productUnit.getName())
                .name("newName")
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE + "ttt")
                .build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenDeleteProductThenDelete() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final ScheduleTemplate scheduleTemplate = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);
        scheduleTemplate.setScheduleTemplateProducts(Set.of(
                ScheduleTemplateProduct.builder()
                        .scheduleTemplate(scheduleTemplate)
                        .product(product)
                        .price(BigDecimal.valueOf(55))
                        .orderNum(1)
                        .limitedAmount(false)
                        .amount(0)
                        .build()
        ));
        scheduleTemplateRepository.save(scheduleTemplate);

        final String url = String.format("/companies/%s/products/%s", company.getUid(), product.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(productRepository.findAll()).isEmpty();
        assertThat(scheduleTemplateRepository.findByIdAndCompanyWithScheduleTemplateProducts(scheduleTemplate.getId(), company)
                .orElseThrow().getScheduleTemplateProducts()).isEmpty();
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenDeleteProductThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s", company.getUid() + "ii", product.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenDeleteProductThen403() throws Exception {
        //given
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s", company.getUid(), product.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenCreateCustomizationsWrapperThenCreate() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s/customizations", company.getUid(), product.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.productCustomizationWrapperRequest(1));

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(2);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenCreateCustomizationsThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s/customizations", company.getUid() + "I", product.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.productCustomizationWrapperRequest(1));


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productCustomizationWrapperRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenCreateCustomizationsThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final String url = String.format("/companies/%s/products/%s/customizations", company.getUid(), product.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.productCustomizationWrapperRequest(1));


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productCustomizationWrapperRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenCreateCustomizatonsThen422() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final String url = String.format("/companies/%s/products/%s/customizations", company.getUid(), product.getId());
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        request.setType(ProductCustomizationWrapperType.SINGLE + "keke");
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        assertThat(productCustomizationWrapperRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateCustomizationsWrapperThenUpdate() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid(), product.getId(), productCustomizationWrapper.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.productCustomizationWrapperRequest(1));

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(2);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateCustomizationsThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid() + "i", product.getId(), productCustomizationWrapper.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.productCustomizationWrapperRequest(1));


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenUpdateCustomizationsThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid(), product.getId(), productCustomizationWrapper.getId());
        final String json = objectMapper.writeValueAsString(ProductDataGenerator.productCustomizationWrapperRequest(1));


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(1);
    }

    @Test
    void givenInvalidRequestOwnCompanyOperationWhenUpdateCustomizatonsThen422() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid(), product.getId(), productCustomizationWrapper.getId());
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        request.setType(ProductCustomizationWrapperType.SINGLE + "keke");
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenDeleteCustomizationsWrapperThenDelete() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid(), product.getId(), productCustomizationWrapper.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(productCustomizationWrapperRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenDeleteCustomizationsThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid() + "i", product.getId(), productCustomizationWrapper.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(1);
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegeWhenDeleteCustomizationsThen403() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));
        final Privilege privilege = privilegeRepository.findAll().stream().filter(t -> t.getName().equalsIgnoreCase(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE))
                .findFirst().orElseThrow();
        privilege.setName("differentPrivilege");
        privilegeRepository.save(privilege);

        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid(), product.getId(), productCustomizationWrapper.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(1);
    }

    @Test
    void givenNotExistingCustomizationOwnCompanyOperationWhenDeleteCustomizatonsThen404() throws Exception {
        //given
        final Product product = productRepository.save(ProductDataGenerator.product(1, productUnit, company));

        final String url = String.format("/companies/%s/products/%s/customizations/%s", company.getUid(), product.getId(), 1234);

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }
}