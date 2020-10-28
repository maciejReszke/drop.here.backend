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
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
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
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShipmentCompanyControllerTest extends IntegrationBaseClass {
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
    private ShipmentProductRepository shipmentProductRepository;

    private Drop drop;
    private Account companyAccount;
    private Company company;
    private RouteProduct routeProduct;
    private Route route;
    private Customer customer;
    private ProductUnit productUnit;
    private Country country;
    private AccountProfile accountProfile;


    @BeforeEach
    void prepare() {
        companyAccount = accountRepository.save(AccountDataGenerator.companyAccount(1));
        country = countryRepository.save(CountryDataGenerator.poland());
        company = companyRepository.save(CompanyDataGenerator.company(1, companyAccount, country));
        final Spot spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        accountProfile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, companyAccount));
        route = RouteDataGenerator.route(1, company);
        route.setWithSeller(true);
        route.setProfile(accountProfile);
        routeRepository.save(route);
        drop = dropRepository.save(DropDataGenerator.drop(1, route, spot));
        final Account customerAccount = accountRepository.save(AccountDataGenerator.customerAccount(2));
        productUnit = productUnitRepository.save(ProductUnitDataGenerator.productUnit(1));
        final Product product = ProductDataGenerator.product(1, productUnit, company)
                .toBuilder()
                .unitFraction(BigDecimal.ONE)
                .build();
        routeProduct = RouteDataGenerator.product(1, route, product);
        routeProduct.setLimitedAmount(true);
        routeProduct.setAmount(BigDecimal.valueOf(150));
        routeProduct.setPrice(BigDecimal.valueOf(13.44));
        routeProductRepository.save(routeProduct);
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE).account(companyAccount).build());
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
    void givenExistingShipmentWhenFindCompanyShipmentThenFind() throws Exception {
        //given
        final Shipment shipment1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1, routeProduct, ProductDataGenerator.product(1, productUnit, company));
        shipment1.getProducts().add(shipmentProduct1);
        shipment1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1);

        final String url = String.format("/companies/%s/shipments/%s", company.getUid(), shipment1.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(companyAccount).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(shipment1.getId().intValue())));
    }

    @Test
    void givenOtherCompanyShipmentWhenFindCompanyShipmentThen404() throws Exception {
        //given

        final Account otherCompanyAccount = accountRepository.save(AccountDataGenerator.companyAccount(3));
        final Company otherCompany = companyRepository.save(CompanyDataGenerator.company(2, otherCompanyAccount, country));
        final Shipment otherCompanyShipment = shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, otherCompany, customer, new HashSet<>()));

        final String url = String.format("/companies/%s/shipments/%s", company.getUid(), otherCompanyShipment.getId());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .param("status", ShipmentStatus.ACCEPTED.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(companyAccount).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }


    @Test
    void givenInvalidPrivilegesWhenFindCompanyShipmentThen403() throws Exception {
        //given
        final Shipment shipment1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1, routeProduct, ProductDataGenerator.product(1, productUnit, company));
        shipment1.getProducts().add(shipmentProduct1);
        shipment1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1);

        final String url = String.format("/companies/%s/shipments/%s", company.getUid(), shipment1.getId());

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(companyAccount).build());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(companyAccount).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    void givenExistingShipmentsWhenFindCompanyShipmentsThenFind() throws Exception {
        //given
        final Shipment shipment1route1drop1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1route1drop1, routeProduct, ProductDataGenerator.product(1, productUnit, company));
        shipment1route1drop1.getProducts().add(shipmentProduct1);
        shipment1route1drop1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1route1drop1);

        final Shipment shipment2route1drop2 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct2 = ShipmentDataGenerator.product(shipment2route1drop2, routeProduct, ProductDataGenerator.product(2, productUnit, company));
        shipment2route1drop2.getProducts().add(shipmentProduct2);
        shipment2route1drop2.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment2route1drop2);

        final Account otherCompanyAccount = accountRepository.save(AccountDataGenerator.companyAccount(3));
        final Company otherCompany = companyRepository.save(CompanyDataGenerator.company(2, otherCompanyAccount, country));
        shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, otherCompany, customer, new HashSet<>()));

        final String url = String.format("/companies/%s/shipments", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(companyAccount).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }

    @Test
    void givenExistingShipmentsByStatusWhenFindCustomerShipmentsThenFind() throws Exception {
        //given
        final Shipment shipment1route1drop1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1route1drop1, routeProduct, ProductDataGenerator.product(1, productUnit, company));
        shipment1route1drop1.getProducts().add(shipmentProduct1);
        shipment1route1drop1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1route1drop1);

        final Shipment shipment2route1drop2 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct2 = ShipmentDataGenerator.product(shipment2route1drop2, routeProduct, ProductDataGenerator.product(2, productUnit, company));
        shipment2route1drop2.getProducts().add(shipmentProduct2);
        shipment2route1drop2.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment2route1drop2);

        final Account otherCompanyAccount = accountRepository.save(AccountDataGenerator.companyAccount(3));
        final Company otherCompany = companyRepository.save(CompanyDataGenerator.company(2, otherCompanyAccount, country));
        shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, otherCompany, customer, new HashSet<>()));

        final String url = String.format("/companies/%s/shipments", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(companyAccount).getToken())
                .param("status", ShipmentStatus.ACCEPTED.name()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(1)));
    }

    @Test
    void givenExistingShipmentsByDropUidWhenFindCustomerShipmentsThenFind() throws Exception {
        //given
        final Shipment shipment1route1drop1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1route1drop1, routeProduct, ProductDataGenerator.product(1, productUnit, company));
        shipment1route1drop1.getProducts().add(shipmentProduct1);
        shipment1route1drop1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1route1drop1);

        final Shipment shipment2route1drop2 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct2 = ShipmentDataGenerator.product(shipment2route1drop2, routeProduct, ProductDataGenerator.product(2, productUnit, company));
        shipment2route1drop2.getProducts().add(shipmentProduct2);
        shipment2route1drop2.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment2route1drop2);

        final Account otherCompanyAccount = accountRepository.save(AccountDataGenerator.companyAccount(3));
        final Company otherCompany = companyRepository.save(CompanyDataGenerator.company(2, otherCompanyAccount, country));
        shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, otherCompany, customer, new HashSet<>()));

        final String url = String.format("/companies/%s/shipments", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(companyAccount).getToken())
                .param("dropUid", drop.getUid()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[*]", Matchers.hasSize(2)));
    }


    @Test
    void givenInvalidPrivilegesWhenFindCustomerShipmentsThen403() throws Exception {
        //given
        final Shipment shipment1route1drop1 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct1 = ShipmentDataGenerator.product(shipment1route1drop1, routeProduct, ProductDataGenerator.product(1, productUnit, company));
        shipment1route1drop1.getProducts().add(shipmentProduct1);
        shipment1route1drop1.setStatus(ShipmentStatus.PLACED);
        shipmentRepository.save(shipment1route1drop1);

        final Shipment shipment2route1drop2 = ShipmentDataGenerator.shipment(1, drop, company, customer, new HashSet<>());
        final ShipmentProduct shipmentProduct2 = ShipmentDataGenerator.product(shipment2route1drop2, routeProduct, ProductDataGenerator.product(2, productUnit, company));
        shipment2route1drop2.getProducts().add(shipmentProduct2);
        shipment2route1drop2.setStatus(ShipmentStatus.ACCEPTED);
        shipmentRepository.save(shipment2route1drop2);

        final Account otherCompanyAccount = accountRepository.save(AccountDataGenerator.companyAccount(3));
        final Company otherCompany = companyRepository.save(CompanyDataGenerator.company(2, otherCompanyAccount, country));
        shipmentRepository.save(ShipmentDataGenerator.shipment(1, drop, otherCompany, customer, new HashSet<>()));

        privilegeRepository.deleteAll();
        privilegeRepository.save(Privilege.builder().name(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE).account(companyAccount).build());

        final String url = String.format("/companies/%s/shipments", company.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(companyAccount).getToken()));

        //then
        result.andExpect(status().isForbidden());
    }


}