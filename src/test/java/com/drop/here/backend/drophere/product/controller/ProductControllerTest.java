package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageRepository;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductUnitDataGenerator;
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
import java.util.List;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProductCustomizationWrapperRepository productCustomizationWrapperRepository;

    @Autowired
    private ImageRepository imageRepository;

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
        productCustomizationWrapperRepository.deleteAll();
        productRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
        productUnitRepository.deleteAll();
        countryRepository.deleteAll();
        imageRepository.deleteAll();
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

        final List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(products.get(0).getId())).get(0).getCustomizations()).hasSize(2);
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
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationWrapperRepository
                .save(ProductDataGenerator.productCustomizationWrapper(1, product));
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
        assertThat(productCustomizationWrapperRepository.findAll()).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductsIdsWithCustomizations(
                List.of(product.getId())).get(0).getCustomizations()).hasSize(2);
        assertThat(productCustomizationWrapperRepository.findAll().get(0).getId()).isNotEqualTo(productCustomizationWrapper.getId());
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
        final Product product = ProductDataGenerator.product(1, productUnit, company);
        final Image image = imageRepository.save(Image.builder().type(ImageType.CUSTOMER_IMAGE).bytes("bytes".getBytes()).build());
        product.setImage(image);
        productRepository.save(product);
        productCustomizationWrapperRepository.save(productCustomizationWrapperRepository.save(
                ProductDataGenerator.productCustomizationWrapper(1, product)));

        final String url = String.format("/companies/%s/products/%s", company.getUid(), product.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(productRepository.findAll()).isEmpty();
        assertThat(imageRepository.findAll()).isEmpty();
        assertThat(productCustomizationWrapperRepository.findAll()).isEmpty();
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
    void givenNotImageWhenGetProductImageThen404() throws Exception {
        //given
        final Product product = ProductDataGenerator.product(1, productUnit, company);
        productRepository.save(product);
        final String url = String.format("/companies/%s/products/%s/images", company.getUid(), product.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequestNotExistingImageWhenUpdateImageThenUpdate() throws Exception {
        //given
        final Product product = ProductDataGenerator.product(1, productUnit, company);
        productRepository.save(product);
        final String url = String.format("/companies/%s/products/%s/images", company.getUid(), product.getId());

        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().account(account).name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).build());

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
        final Image image = imageRepository.save(Image.builder().type(ImageType.CUSTOMER_IMAGE).bytes("bytes".getBytes()).build());
        final Product product = ProductDataGenerator.product(1, productUnit, company);
        product.setImage(image);
        productRepository.save(product);
        final String url = String.format("/companies/%s/products/%s/images", company.getUid(), product.getId());
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().account(account).name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).build());

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
    void givenValidRequestInvalidPrivilegeWhenUpdateImageThen403() throws Exception {
        //given
        final Product product = ProductDataGenerator.product(1, productUnit, company);
        productRepository.save(product);
        final String url = String.format("/companies/%s/products/%s/images", company.getUid(), product.getId());
        final byte[] bytes = new FileInputStream(new ClassPathResource("imageTest/validImage").getFile()).readAllBytes();
        final MockMultipartFile file = new MockMultipartFile("image", bytes);

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().account(account).name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).build());

        //when
        final ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        perform.andExpect(status().isForbidden());
        assertThat(imageRepository.findAll()).isEmpty();
    }
}