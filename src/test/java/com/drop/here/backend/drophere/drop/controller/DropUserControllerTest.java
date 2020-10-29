package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.repository.AccountProfileRepository;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyCustomerRelationshipRepository;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.repository.CustomerRepository;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
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
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.drop.here.backend.drophere.authentication.account.service.PrivilegeService.CUSTOMER_CREATED_PRIVILEGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DropUserControllerTest extends IntegrationBaseClass {
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
    private SpotRepository spotRepository;

    @Autowired
    private SpotMembershipRepository spotMembershipRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyCustomerRelationshipRepository companyCustomerRelationshipRepository;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private RouteProductRepository routeProductRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    private Company company;
    private Account account;
    private Spot spot;
    private Customer customer;
    private Country country;
    private Drop drop;
    private Route route;

    @BeforeEach
    void prepare() {
        country = countryRepository.save(CountryDataGenerator.poland());
        account = accountRepository.save(AccountDataGenerator.customerAccount(1));
        privilegeRepository.save(Privilege.builder().name(CUSTOMER_CREATED_PRIVILEGE).account(account).build());
        company = companyRepository.save(CompanyDataGenerator.company(1, account, country));
        customer = customerRepository.save(CustomerDataGenerator.customer(1, account));
        spot = spotRepository.save(SpotDataGenerator.spot(1, company));
        final ProductUnit unit = productUnitRepository.save(ProductUnitDataGenerator.productUnit(1));
        final Product product = ProductDataGenerator.product(1, unit, company);
        final Product product2 = ProductDataGenerator.product(2, unit, company);
        route = RouteDataGenerator.route(1, company);
        route.setProducts(List.of(RouteDataGenerator.product(1, route, product), RouteDataGenerator.product(2, route, product2)));
        routeRepository.save(route);
        drop = dropRepository.save(DropDataGenerator.drop(1, route, spot));
    }

    @AfterEach
    void cleanUp() {
        dropRepository.deleteAll();
        routeRepository.deleteAll();
        companyCustomerRelationshipRepository.deleteAll();
        spotMembershipRepository.deleteAll();
        spotRepository.deleteAll();
        routeProductRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountProfileRepository.deleteAll();
        accountRepository.deleteAll();
        countryRepository.deleteAll();
        productUnitRepository.deleteAll();
    }

    @Test
    void givenExistingDropVisibleCompanyNotHiddenNotMembershipNotBlockedWhenFindDropWithoutProfileThenFind() throws Exception {
        //given
        final String url = String.format("/drops/%s", drop.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", Matchers.equalTo(drop.getUid())))
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.profileUid", Matchers.nullValue()))
                .andExpect(jsonPath("$.products", Matchers.hasSize(2)));
    }

    @Test
    void givenExistingDropVisibleCompanyNotHiddenNotMembershipNotBlockedWhenFindDropWithProfileThenFind() throws Exception {
        //given
        final AccountProfile profile = accountProfileRepository.save(AccountProfileDataGenerator.accountProfile(1, account));
        final Route route = routeRepository.findById(this.route.getId()).orElseThrow();
        route.setProfile(profile);
        route.setWithSeller(true);
        routeRepository.save(route);

        final String url = String.format("/drops/%s", drop.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", Matchers.equalTo(drop.getUid())))
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.profileUid", Matchers.equalTo(profile.getProfileUid())))
                .andExpect(jsonPath("$.products", Matchers.hasSize(2)));
    }

    @Test
    void givenExistingDropNotVisibleCompanyNotHiddenNotMembershipNotBlockedWhenFindDropThen404() throws Exception {
        //given
        company.setVisibilityStatus(CompanyVisibilityStatus.HIDDEN);
        companyRepository.save(company);
        final String url = String.format("/drops/%s", drop.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingDropVisibleCompanyHiddenNotMembershipNotBlockedWhenFindDropThen404() throws Exception {
        //given
        spot.setHidden(true);
        spotRepository.save(spot);
        final String url = String.format("/drops/%s", drop.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingDropVisibleCompanyHiddenMembershipActiveNotBlockedWhenFindDropThenFind() throws Exception {
        //given
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.ACTIVE);
        spotMembershipRepository.save(membership);
        final String url = String.format("/drops/%s", drop.getUid());

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", Matchers.equalTo(drop.getUid())))
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.profileUid", Matchers.nullValue()))
                .andExpect(jsonPath("$.products", Matchers.hasSize(2)));
    }

    @Test
    void givenExistingDropVisibleCompanyHiddenMembershipPendingNotBlockedWhenFindDropThen404() throws Exception {
        //given
        final String url = String.format("/drops/%s", drop.getUid());
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.PENDING);
        spotMembershipRepository.save(membership);
        spot.setHidden(true);
        spotRepository.save(spot);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingDropVisibleCompanyNotHiddenMembershipPendingNotBlockedWhenFindDropThenFind() throws Exception {
        //given
        final String url = String.format("/drops/%s", drop.getUid());
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.PENDING);
        spotMembershipRepository.save(membership);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", Matchers.equalTo(drop.getUid())))
                .andExpect(jsonPath("$.spot.uid", Matchers.equalTo(spot.getUid())))
                .andExpect(jsonPath("$.profileUid", Matchers.nullValue()))
                .andExpect(jsonPath("$.products", Matchers.hasSize(2)));
    }

    @Test
    void givenExistingDropVisibleCompanyNotHiddenMembershipBlockedNotBlockedWhenFindDropThen404() throws Exception {
        //given
        final String url = String.format("/drops/%s", drop.getUid());
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        membership.setMembershipStatus(SpotMembershipStatus.BLOCKED);
        spotMembershipRepository.save(membership);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenExistingDropVisibleCompanyNotHiddenNoMembershipBlockedWhenFindDropThen404() throws Exception {
        //given
        final String url = String.format("/drops/%s", drop.getUid());
        final CompanyCustomerRelationship relationship = CompanyDataGenerator.companyCustomerRelationship(company, customer);
        relationship.setRelationshipStatus(CompanyCustomerRelationshipStatus.BLOCKED);
        companyCustomerRelationshipRepository.save(relationship);

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.createToken(account).getToken()));

        //then
        result.andExpect(status().isNotFound());
    }
}