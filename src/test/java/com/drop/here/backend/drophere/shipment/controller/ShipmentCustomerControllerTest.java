package com.drop.here.backend.drophere.shipment.controller;

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
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationJobRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomizationRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProductRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.enums.ShipmentCustomerDecision;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.repository.ShipmentFlowRepository;
import com.drop.here.backend.drophere.shipment.repository.ShipmentProductRepository;
import com.drop.here.backend.drophere.shipment.repository.ShipmentRepository;
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
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductUnitDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import com.drop.here.backend.drophere.test_data.ShipmentDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShipmentCustomerControllerTest extends IntegrationBaseClass {
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

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private NotificationJobRepository notificationJobRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SpotMembershipRepository spotMembershipRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentFlowRepository shipmentFlowRepository;

    @Autowired
    private ShipmentProductRepository shipmentProductRepository;

    private Drop drop;
    private Account customerAccount;
    private Company company;
    private RouteProduct routeProduct;
    private Route route;
    private Customer customer;
    private ProductUnit productUnit;
    private ProductCustomization productCustomization;


    @BeforeEach
    void prepare() {
        final Account companyAccount = accountRepository.save(AccountDataGenerator.companyAccount(1));
        final Country country = countryRepository.save(CountryDataGenerator.poland());
        company = companyRepository.save(CompanyDataGenerator.company(1, companyAccount, country));
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final AccountProfile accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, companyAccount));
        route = RouteDataGenerator.route(1, company);
        route.setWithSeller(true);
        route.setProfile(accountProfile);
        routeRepository.save(route);
        drop = dropRepository.save(DropDataGenerator.drop(1, route, spot));
        customerAccount = accountRepository.save(AccountDataGenerator.customerAccount(2));
        productUnit = productUnitRepository.save(ProductUnitDataGenerator.productUnit(1));
        final Product product = ProductDataGenerator.product(1, productUnit, company)
                .toBuilder()
                .unitFraction(BigDecimal.ONE)
                .build();
        final ProductCustomizationWrapper customizationWrapper = ProductDataGenerator.productCustomizationWrapper(1, product);
        product.setCustomizationWrappers(List.of(customizationWrapper));
        productCustomization = (ProductCustomization) customizationWrapper.getCustomizations().toArray()[0];
        routeProduct = RouteDataGenerator.product(1, route, product);
        routeProduct.setLimitedAmount(true);
        routeProduct.setAmount(BigDecimal.valueOf(150));
        routeProduct.setPrice(BigDecimal.valueOf(13.44));
        routeProductRepository.save(routeProduct);
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE).account(customerAccount).build());
        customer = customerRepository.save(CustomerDataGenerator.customer(1, customerAccount));

        notificationTokenRepository.save(NotificationToken.builder()
                .tokenType(NotificationTokenType.PROFILE)
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE)
                .ownerAccountProfile(accountProfile)
                .token("token123")
                .build());

        notificationTokenRepository.save(NotificationToken.builder()
                .tokenType(NotificationTokenType.CUSTOMER)
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE)
                .ownerCustomer(customer)
                .token("token123")
                .build());

        spotMembershipRepository.save(SpotDataGenerator.membership(spot, customer));


    }

    @AfterEach
    void cleanUp() {
        spotMembershipRepository.deleteAll();
        shipmentFlowRepository.deleteAll();
        shipmentRepository.deleteAll();
        notificationJobRepository.deleteAll();
        notificationRepository.deleteAll();
        notificationTokenRepository.deleteAll();
        routeRepository.deleteAll();
        spotRepository.deleteAll();
        dropRepository.deleteAll();
        productCustomizationWrapperRepository.deleteAll();
        productRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        customerRepository.deleteAll();
        accountRepository.deleteAll();
        productUnitRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void givenValidRequestAutomaticallyAcceptedWithoutCustomizationsWhenCreateShipmentThenCreate() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(true);
        routeRepository.save(route);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        final String url = String.format("/companies/%s/drops/%s/shipments", company.getUid(), drop.getUid());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(notificationJobRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll()).hasSize(1);
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.ACCEPTED);
        assertThat(savedShipment.getSummarizedAmount()).isEqualByComparingTo(BigDecimal.valueOf(40.32));
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(147));
        assertThat(shipmentFlowRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestAutomaticallyAcceptedWithCustomizationsWhenCreateShipmentThenCreate() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(true);
        routeRepository.save(route);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of(ShipmentCustomizationRequest.builder()
                                .id(productCustomization.getId())
                                .build()))
                        .build()))
                .comment("comment123")
                .build();
        final String url = String.format("/companies/%s/drops/%s/shipments", company.getUid(), drop.getUid());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(notificationJobRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll()).hasSize(1);
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.ACCEPTED);
        assertThat(savedShipment.getSummarizedAmount()).isEqualByComparingTo(BigDecimal.valueOf(49.32));
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(147));
        assertThat(shipmentFlowRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestNotAutomaticallyAcceptedWhenCreateShipmentThenCreate() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(false);
        routeRepository.save(route);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        final String url = String.format("/companies/%s/drops/%s/shipments", company.getUid(), drop.getUid());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isCreated());

        assertThat(notificationJobRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll()).hasSize(1);
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(savedShipment.getSummarizedAmount()).isEqualByComparingTo(BigDecimal.valueOf(40.32));
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestAutomaticallyAcceptedNotEnoughProductsWhenCreateShipmentThen601() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(true);
        routeRepository.save(route);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(152))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        final String url = String.format("/companies/%s/drops/%s/shipments", company.getUid(), drop.getUid());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().is(601));

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).isEmpty();
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidRolesWhenCreateShipmentThen403() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(true);
        routeRepository.save(route);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(customerAccount).build());
        final String url = String.format("/companies/%s/drops/%s/shipments", company.getUid(), drop.getUid());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).isEmpty();
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenLackOfSpotMembershipWhenCreateShipmentThen404() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(true);
        routeRepository.save(route);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(11))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        spotMembershipRepository.deleteAll();
        final String url = String.format("/companies/%s/drops/%s/shipments", company.getUid(), drop.getUid());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).isEmpty();
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestWhenUpdateShipmentThenUpdate() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(false);
        routeRepository.save(route);
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(notificationJobRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll()).hasSize(1);
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(savedShipment.getSummarizedAmount()).isEqualByComparingTo(BigDecimal.valueOf(40.32));
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentProductRepository.findAll()).hasSize(1);
        assertThat(shipmentProductRepository.findById(((ShipmentProduct) shipment.getProducts().toArray()[0]).getId())).isEmpty();
        assertThat(shipmentFlowRepository.findAll()).hasSize(1);
    }

    @Test
    void givenValidRequestInvalidStatusWhenUpdateShipmentThen422() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(false);
        routeRepository.save(route);
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isUnprocessableEntity());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.ACCEPTED);
        assertThat(savedShipment.getSummarizedAmount()).isEqualByComparingTo(shipment.getSummarizedAmount());
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentProductRepository.findAll()).hasSize(1);
        assertThat(shipmentProductRepository.findById(((ShipmentProduct) shipment.getProducts().toArray()[0]).getId())).isPresent();
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestInvalidPrivilegesWhenUpdateShipmentThen403() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(false);
        routeRepository.save(route);
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(customerAccount).build());
        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(savedShipment.getSummarizedAmount()).isEqualByComparingTo(shipment.getSummarizedAmount());
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentProductRepository.findAll()).hasSize(1);
        assertThat(shipmentProductRepository.findById(((ShipmentProduct) shipment.getProducts().toArray()[0]).getId())).isPresent();
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenValidRequestNotOwnShipmentWhenUpdateShipmentThen404() throws Exception {
        //given
        route.setAcceptShipmentsAutomatically(false);
        routeRepository.save(route);
        final Account otherCustomerAccount = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer otherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, otherCustomerAccount));
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, otherCustomer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment);
        final ShipmentCustomerSubmissionRequest request = ShipmentCustomerSubmissionRequest.builder()
                .products(List.of(ShipmentProductRequest.builder()
                        .routeProductId(routeProduct.getId())
                        .quantity(BigDecimal.valueOf(3))
                        .customizations(List.of())
                        .build()))
                .comment("comment123")
                .build();
        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);


        //when
        final ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(savedShipment.getSummarizedAmount()).isEqualByComparingTo(shipment.getSummarizedAmount());
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentProductRepository.findAll()).hasSize(1);
        assertThat(shipmentProductRepository.findById(((ShipmentProduct) shipment.getProducts().toArray()[0]).getId())).isPresent();
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenPlacedShipmentCancelDecisionWhenUpdateStatusThenUpdate() throws Exception {
        //given
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment);

        final ShipmentCustomerDecisionRequest request = ShipmentCustomerDecisionRequest.builder()
                .comment("comment123")
                .customerDecision(ShipmentCustomerDecision.CANCEL)
                .build();

        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(notificationJobRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll()).hasSize(1);
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.CANCELLED);
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).hasSize(1);
    }

    @Test
    void givenAcceptedShipmentCancelDecisionWhenUpdateStatusThenUpdate() throws Exception {
        //given
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment);

        final ShipmentCustomerDecisionRequest request = ShipmentCustomerDecisionRequest.builder()
                .comment("comment123")
                .customerDecision(ShipmentCustomerDecision.CANCEL)
                .build();

        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isOk());

        assertThat(notificationJobRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll()).hasSize(1);
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.CANCEL_REQUESTED);
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).hasSize(1);
    }

    @Test
    void givenInvalidShipmentStatusCancelDecisionWhenUpdateStatusThen422() throws Exception {
        //given
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipmentRepository.save(shipment);

        final ShipmentCustomerDecisionRequest request = ShipmentCustomerDecisionRequest.builder()
                .comment("comment123")
                .customerDecision(ShipmentCustomerDecision.CANCEL)
                .build();

        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isUnprocessableEntity());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenInvalidPrivilegesWhenUpdateStatusThen403() throws Exception {
        //given
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment);

        final ShipmentCustomerDecisionRequest request = ShipmentCustomerDecisionRequest.builder()
                .comment("comment123")
                .customerDecision(ShipmentCustomerDecision.CANCEL)
                .build();

        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(customerAccount).build());

        //when
        final ResultActions result = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isForbidden());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenNotOwnShipmentWhenUpdateStatusThen404() throws Exception {
        //given
        final Account otherCustomerAccount = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer otherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, otherCustomerAccount));
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, otherCustomer, new HashSet<>());
        final ShipmentProduct shipmentProduct = ShipmentDataGenerator.product(shipment, routeProduct, routeProduct.getProduct());
        shipment.getProducts().add(shipmentProduct);
        shipment.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment);


        final ShipmentCustomerDecisionRequest request = ShipmentCustomerDecisionRequest.builder()
                .comment("comment123")
                .customerDecision(ShipmentCustomerDecision.CANCEL)
                .build();

        final String url = String.format("/companies/%s/drops/%s/shipments/%s", company.getUid(), drop.getUid(), shipment.getId());
        final String json = objectMapper.writeValueAsString(request);

        //when
        final ResultActions result = mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isNotFound());

        assertThat(notificationJobRepository.findAll()).isEmpty();
        assertThat(notificationRepository.findAll()).isEmpty();
        final List<Shipment> shipments = shipmentRepository.findAll();
        assertThat(shipments).hasSize(1);
        final Shipment savedShipment = shipments.get(0);
        assertThat(savedShipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(routeProductRepository.findById(routeProduct.getId()).orElseThrow().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(shipmentFlowRepository.findAll()).isEmpty();
    }

    @Test
    void givenExistingShipmentsWhenFindCustomerShipmentsThenFind() throws Exception {
        //given
        final Shipment shipment1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1, routeProduct, routeProduct.getProduct());
        shipment1.getProducts().add(shipmentProduct1);
        shipment1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1);

        final Shipment shipment2 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct2 = ShipmentDataGenerator.product(shipment2, routeProduct, routeProduct.getProduct());
        shipment2.getProducts().add(shipmentProduct2);
        shipment2.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment2);

        final Account otherCustomerAccount = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer otherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, otherCustomerAccount));
        shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, company, otherCustomer, new HashSet<>()));

        final String url = "/shipments";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenExistingShipmentsByStatusWhenFindCustomerShipmentsThenFind() throws Exception {
        //given
        final Shipment shipment1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1, routeProduct, routeProduct.getProduct());
        shipment1.getProducts().add(shipmentProduct1);
        shipment1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1);

        final Shipment shipment2 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct2 = ShipmentDataGenerator.product(shipment2, routeProduct, routeProduct.getProduct());
        shipment2.getProducts().add(shipmentProduct2);
        shipment2.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment2);

        final Account otherCustomerAccount = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer otherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, otherCustomerAccount));
        shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, company, otherCustomer, new HashSet<>()));

        final String url = "/shipments";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("status", ShipmentStatus.ACCEPTED.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }


    @Test
    void givenInvalidPrivilegesWhenFindCustomerShipmentsThen403() throws Exception {
        //given
        final Shipment shipment1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1, routeProduct, routeProduct.getProduct());
        shipment1.getProducts().add(shipmentProduct1);
        shipment1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1);

        final Shipment shipment2 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct2 = ShipmentDataGenerator.product(shipment2, routeProduct, routeProduct.getProduct());
        shipment2.getProducts().add(shipmentProduct2);
        shipment2.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment2);

        final Account otherCustomerAccount = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer otherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, otherCustomerAccount));
        shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, company, otherCustomer, new HashSet<>()));

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(customerAccount).build());

        final String url = "/shipments";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenExistingShipmentWhenFindCustomerShipmentThenFind() throws Exception {
        //given
        final Shipment shipment1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1, routeProduct, routeProduct.getProduct());
        shipment1.getProducts().add(shipmentProduct1);
        shipment1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1);

        final String url = String.format("/shipments/%s", shipment1.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(shipment1.getId().intValue())));
    }

    @Test
    void givenOtherCustomerShipmentWhenFindCustomerShipmentThen404() throws Exception {
        //given

        final Account otherCustomerAccount = accountRepository.save(AccountDataGenerator.customerAccount(3));
        final Customer otherCustomer = customerRepository.save(CustomerDataGenerator.customer(2, otherCustomerAccount));
        final Shipment otherCustomerShipment = shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, company, otherCustomer, new HashSet<>()));

        final String url = String.format("/shipments/%s", otherCustomerShipment.getId());


        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("status", ShipmentStatus.ACCEPTED.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }


    @Test
    void givenInvalidPrivilegesWhenFindCustomerShipmentThen403() throws Exception {
        //given
        final Shipment shipment1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1, routeProduct, routeProduct.getProduct());
        shipment1.getProducts().add(shipmentProduct1);
        shipment1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1);

        final String url = String.format("/shipments/%s", shipment1.getId());

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(customerAccount).build());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(customerAccount).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

}