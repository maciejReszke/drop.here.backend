package com.drop.here.backend.drophere.route.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.route.dto.RouteDropRequest;
import com.drop.here.backend.drophere.route.dto.RouteProductRequest;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.route.dto.UnpreparedRouteRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.test_config.IntegrationBaseClass;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductUnitDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.util.List;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RouteControllerTest extends IntegrationBaseClass {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private ProductCustomizationWrapperRepository productCustomizationWrapperRepository;

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
    private RouteProductRepository routeProductRepository;

    private Company company;
    private Account account;
    private Product product;
    private Spot spot;
    private AccountProfile accountProfile;
    private ProductUnit unit;

    @BeforeEach
    void prepare() {
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.companyAccount(1));
        privilegeRepository.save(Privilege.builder().name(COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        unit = productUnitRepository.save(ProductUnitDataGenerator.productUnit(1));
        final Product preSavedProduct = ProductDataGenerator.product(1, unit, company);
        preSavedProduct.setCustomizationWrappers(List.of(ProductDataGenerator.productCustomizationWrapper(1, preSavedProduct)));
        product = productRepository.save(preSavedProduct);
        spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
    }


    @AfterEach
    void cleanUp() {
        routeRepository.deleteAll();
        spotRepository.deleteAll();
        productCustomizationWrapperRepository.deleteAll();
        productRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
        productUnitRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenCreateRouteThenCreate() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setProductId(product.getId());
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        routeRequest.setProfileUid(accountProfile.getProfileUid());

        final String url = String.format("/companies/%s/routes", company.getUid());
        final String json = objectMapper.writeValueAsString(routeRequest);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(routeRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll()).hasSize(1);
        assertThat(routeProductRepository.findAll()).hasSize(1);
        assertThat(productRepository.findAll()).hasSize(2);
        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(product)).hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(product)
                .get(0).getCustomizations()).hasSize(1);

        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(productRepository.findAll().get(1)))
                .hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(productRepository.findAll().get(1))
                .get(0).getCustomizations()).hasSize(1);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenCreateRouteThenForbidden() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setProductId(product.getId());
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        final String url = String.format("/companies/%s/routes", company.getUid() + "kek");
        final String json = objectMapper.writeValueAsString(routeRequest);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(routeRepository.findAll()).isEmpty();
        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(routeProductRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegesWhenCreateRouteThenForbidden() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setProductId(product.getId());
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        final String url = String.format("/companies/%s/routes", company.getUid());
        final String json = objectMapper.writeValueAsString(routeRequest);
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

        assertThat(routeRepository.findAll()).isEmpty();
        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(routeProductRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRequestInvalidAmountWhenLimitedOwnCompanyOperationWhenCreateRouteThen422() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setLimitedAmount(true);
        routeProductRequest.setAmount(null);
        routeProductRequest.setProductId(product.getId());
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        final String url = String.format("/companies/%s/routes", company.getUid());
        final String json = objectMapper.writeValueAsString(routeRequest);

        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

        assertThat(routeRepository.findAll()).isEmpty();
        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(routeProductRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenUpdateRouteThenUpdate() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setProductId(product.getId());
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        routeRequest.setName("name123");
        routeRequest.setProfileUid(accountProfile.getProfileUid());

        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        final Product newProduct = ProductDataGenerator.product(1, unit, company);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, newProduct)));
        final Route route = routeRepository.save(preSavedRoute);
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId());
        final String json = objectMapper.writeValueAsString(RouteRequest.builder().unpreparedRouteRequest(routeRequest).build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(routeRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll()).hasSize(1);
        assertThat(routeProductRepository.findAll()).hasSize(1);
        assertThat(routeRepository.findAll().get(0).getName()).isEqualTo("name123");
        assertThat(productRepository.findAll()).hasSize(2);
        assertThat(productRepository.findById(product.getId())).isNotEmpty();
        assertThat(productRepository.findById(newProduct.getId())).isEmpty();
        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(product))
                .hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(newProduct))
                .isEmpty();

        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(productRepository.findAll().get(1)))
                .hasSize(1);
        assertThat(productCustomizationWrapperRepository.findByProductWithCustomizations(productRepository.findAll().get(1))
                .get(0).getCustomizations()).hasSize(1);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenUpdateRouteThenForbidden() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setProductId(product.getId());
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        routeRequest.setName("name123");

        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final String url = String.format("/companies/%s/routes/%s", company.getUid() + "kek", route.getId());
        final String json = objectMapper.writeValueAsString(RouteRequest.builder().unpreparedRouteRequest(routeRequest).build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(routeRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(routeProductRepository.findAll()).isEmpty();
        assertThat(routeRepository.findAll().get(0).getName()).isNotEqualTo("name123");
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegesWhenUpdateRouteThenForbidden() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setProductId(product.getId());
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        routeRequest.setName("name123");

        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId());
        final String json = objectMapper.writeValueAsString(RouteRequest.builder().unpreparedRouteRequest(routeRequest).build());
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

        assertThat(routeRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(routeProductRepository.findAll()).isEmpty();
        assertThat(routeRepository.findAll().get(0).getName()).isNotEqualTo("name123");
    }

    @Test
    void givenInvalidRequestProductNotFoundOwnCompanyOperationWhenUpdateRouteThen404() throws Exception {
        //given
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final RouteProductRequest routeProductRequest = routeRequest.getProducts().get(0);
        routeProductRequest.setProductId(product.getId() + 5);
        routeRequest.setProducts(List.of(routeProductRequest));
        final RouteDropRequest dropRequest = routeRequest.getDrops().get(0);
        dropRequest.setSpotId(spot.getId());
        routeRequest.setName("name123");

        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId());
        final String json = objectMapper.writeValueAsString(RouteRequest.builder().unpreparedRouteRequest(routeRequest).build());

        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
        assertThat(routeRepository.findAll()).hasSize(1);
        assertThat(dropRepository.findAll()).isEmpty();
        assertThat(routeProductRepository.findAll()).isEmpty();
        assertThat(routeRepository.findAll().get(0).getName()).isNotEqualTo("name123");
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenDeleteRouteThenDelete() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        final Product newProduct = ProductDataGenerator.product(1, unit, company);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, newProduct)));
        final Route route = routeRepository.save(preSavedRoute);
        assumeThat(productRepository.findAll()).hasSize(2);
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(routeRepository.findAll()).isEmpty();
        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotOwnCompanyOperationWhenDeleteRouteThenForbidden() throws Exception {
        //given
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final String url = String.format("/companies/%s/routes/%s", company.getUid() + "kek", route.getId());

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(routeRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationInvalidPrivilegesWhenDeleteRouteThenForbidden() throws Exception {
        //given
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId());
        final Privilege privilege = privilegeRepository.findAll().get(0);
        privilege.setName("kaka");
        privilegeRepository.save(privilege);

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(routeRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenInvalidRequestProductNotFoundOwnCompanyOperationWhenDeleteRouteThen404() throws Exception {
        //given
        final Route route = routeRepository.save(RouteDataGenerator.route(1, company));
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId() + 15);

        //when
        final ResultActions result = mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(routeRepository.findAll()).isNotEmpty();
    }

    @Test
    void givenValidRequestOwnCompanyOperationWhenFindRouteThenFind() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        final Route route = routeRepository.save(preSavedRoute);
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()));
    }

    @Test
    void givenNotOwnCompanyOperationWhenFindRouteThenForbidden() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        final Route route = routeRepository.save(preSavedRoute);
        final String url = String.format("/companies/%s/routes/%s", company.getUid() + "kek", route.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidPrivilegesWhenFindRouteThenForbidden() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        final Route route = routeRepository.save(preSavedRoute);
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId());

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
    void givenNotExistingRouteOwnCompanyOperationWhenFindRouteThen404() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        final Route route = routeRepository.save(preSavedRoute);
        final String url = String.format("/companies/%s/routes/%s", company.getUid(), route.getId() + 15);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequestOwnCompanyOperationNoParamsWhenFindRoutesThenFind() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setStatus(RouteStatus.UNPREPARED);
        final Route preSavedRoute2 = RouteDataGenerator.route(2, company);
        preSavedRoute2.setStatus(RouteStatus.ONGOING);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        routeRepository.save(preSavedRoute);
        routeRepository.save(preSavedRoute2);
        final String url = String.format("/companies/%s/routes", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenValidRequestOwnCompanyOperationByParamWhenFindRoutesThenFind() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setStatus(RouteStatus.UNPREPARED);
        final Route preSavedRoute2 = RouteDataGenerator.route(1, company);
        preSavedRoute2.setStatus(RouteStatus.ONGOING);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        routeRepository.save(preSavedRoute);
        routeRepository.save(preSavedRoute2);
        final String url = String.format("/companies/%s/routes", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("routeStatus", RouteStatus.UNPREPARED.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenNotOwnCompanyOperationWhenFindRoutesThenForbidden() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        routeRepository.save(preSavedRoute);
        final String url = String.format("/companies/%s/routes", company.getUid() + "kek");

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidPrivilegesWhenFindRoutesThenForbidden() throws Exception {
        //given
        final Route preSavedRoute = RouteDataGenerator.route(1, company);
        preSavedRoute.setDrops(List.of(DropDataGenerator.drop(1, preSavedRoute, spot)));
        preSavedRoute.setProfile(accountProfile);
        preSavedRoute.setProducts(List.of(RouteDataGenerator.product(1, preSavedRoute, ProductDataGenerator
                .product(1, unit, company))));
        routeRepository.save(preSavedRoute);
        final String url = String.format("/companies/%s/routes", company.getUid());

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