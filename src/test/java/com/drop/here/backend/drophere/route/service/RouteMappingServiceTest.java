package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfilePersistenceService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.route.dto.RouteProductRequest;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.service.SpotPersistenceService;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteMappingServiceTest {

    @InjectMocks
    private RouteMappingService routeMappingService;

    @Mock
    private AccountProfilePersistenceService accountProfilePersistenceService;

    @Mock
    private SpotPersistenceService spotPersistenceService;

    @Mock
    private ProductService productService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(routeMappingService, "namePartLength", 6, true);
        FieldUtils.writeDeclaredField(routeMappingService, "randomPartLength", 6, true);
    }

    @Test
    void givenValidRequestWhenToRouteThenMap() {
        //given
        final RouteRequest routeRequest = RouteDataGenerator.request(1);
        final Company company = CompanyDataGenerator.company(1, null, null);

        final Product product = Product.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        when(productService.getProduct(any(), any())).thenReturn(product);
        when(accountProfilePersistenceService.findActiveByCompanyAndProfileUid(company, routeRequest.getProfileUid()))
                .thenReturn(Optional.of(accountProfile));
        final Spot spot = Spot.builder().build();
        when(spotPersistenceService.findSpot(any(), any(Company.class))).thenReturn(spot);

        //when
        final Route response = routeMappingService.toRoute(routeRequest, company);

        //then
        assertThat(response.getCompany()).isEqualTo(company);
        assertThat(response.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(response.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(response.getName()).isEqualTo(routeRequest.getName());
        assertThat(response.getDescription()).isEqualTo(routeRequest.getDescription());
        assertThat(response.getStatus()).isEqualTo(RouteStatus.PREPARED);
        assertThat(response.getProfile()).isEqualTo(accountProfile);
        assertThat(response.getRouteDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).isEqualTo(routeRequest.getDate());
        assertThat(response.getProducts()).hasSize(2);
        final RouteProduct product1 = response.getProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow();
        final RouteProductRequest requestProduct1 = routeRequest.getProducts().get(0);
        assertThat(product1.getAmount()).isEqualTo(requestProduct1.getAmount());
        assertThat(product1.getPrice()).isEqualTo(requestProduct1.getPrice());
        assertThat(product1.getProduct()).isEqualTo(product);
        assertThat(product1.isLimitedAmount()).isTrue();
        assertThat(product1.getRoute()).isEqualTo(response);
        final RouteProduct product2 = response.getProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow();
        final RouteProductRequest requestProduct2 = routeRequest.getProducts().get(1);
        assertThat(product2.getAmount()).isEqualTo(requestProduct2.getAmount());
        assertThat(product2.getPrice()).isEqualTo(requestProduct2.getPrice());
        assertThat(product2.getProduct()).isEqualTo(product);
        assertThat(product2.getRoute()).isEqualTo(response);
        assertThat(product2.isLimitedAmount()).isTrue();
        assertThat(response.getDrops()).hasSize(1);
        assertThat(response.getDrops().get(0).getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(response.getDrops().get(0).getDescription()).isEqualTo(routeRequest.getDescription());
        assertThat(response.getDrops().get(0).getEndTime()).isNotNull();
        assertThat(response.getDrops().get(0).getName()).isEqualTo(routeRequest.getName());
        assertThat(response.getDrops().get(0).getStartTime()).isNotNull();
        assertThat(response.getDrops().get(0).getUid()).startsWith(routeRequest.getName().substring(0, 6));
        assertThat(response.getDrops().get(0).getUid()).isNotEqualTo(routeRequest.getName().substring(0, 12));
        assertThat(response.getDrops().get(0).getUid()).hasSize(12);
        assertThat(response.getDrops().get(0).getSpot()).isEqualTo(spot);
        assertThat(response.getDrops().get(0).getRoute()).isEqualTo(response);
        assertThat(response.getDrops().get(0).getStatus()).isEqualTo(DropStatus.PREPARED);
    }

    @Test
    void givenRequestAndRouteWhenUpdateRouteThenUpdate() {
        //given
        final RouteRequest routeRequest = RouteDataGenerator.request(1);
        final Company company = CompanyDataGenerator.company(1, null, null);

        final Product product = Product.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        when(productService.getProduct(any(), any())).thenReturn(product);
        when(accountProfilePersistenceService.findActiveByCompanyAndProfileUid(company, routeRequest.getProfileUid()))
                .thenReturn(Optional.of(accountProfile));
        final Spot spot = Spot.builder().build();
        when(spotPersistenceService.findSpot(any(), any(Company.class))).thenReturn(spot);
        final Route route = Route.builder().build();
        final Drop prevDrop = Drop.builder().route(route).build();
        final RouteProduct prevProduct = RouteProduct.builder().route(route).build();
        route.setDrops(List.of(prevDrop));
        route.setProducts(List.of(prevProduct));

        //when
        routeMappingService.updateRoute(route, routeRequest, company);

        //then
        assertThat(route.getCompany()).isEqualTo(company);
        assertThat(route.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(route.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(route.getName()).isEqualTo(routeRequest.getName());
        assertThat(route.getDescription()).isEqualTo(routeRequest.getDescription());
        assertThat(route.getStatus()).isEqualTo(RouteStatus.PREPARED);
        assertThat(route.getProfile()).isEqualTo(accountProfile);
        assertThat(route.getRouteDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).isEqualTo(routeRequest.getDate());
        assertThat(route.getProducts()).hasSize(2);
        final RouteProduct product1 = route.getProducts().stream().filter(t -> t.getOrderNum() == 1).findFirst().orElseThrow();
        final RouteProductRequest requestProduct1 = routeRequest.getProducts().get(0);
        assertThat(product1.getAmount()).isEqualTo(requestProduct1.getAmount());
        assertThat(product1.getPrice()).isEqualTo(requestProduct1.getPrice());
        assertThat(product1.getProduct()).isEqualTo(product);
        assertThat(product1.isLimitedAmount()).isTrue();
        assertThat(product1.getRoute()).isEqualTo(route);
        final RouteProduct product2 = route.getProducts().stream().filter(t -> t.getOrderNum() == 2).findFirst().orElseThrow();
        final RouteProductRequest requestProduct2 = routeRequest.getProducts().get(1);
        assertThat(product2.getAmount()).isEqualTo(requestProduct2.getAmount());
        assertThat(product2.getPrice()).isEqualTo(requestProduct2.getPrice());
        assertThat(product2.getProduct()).isEqualTo(product);
        assertThat(product2.getRoute()).isEqualTo(route);
        assertThat(product2.isLimitedAmount()).isTrue();
        assertThat(route.getDrops()).hasSize(1);
        assertThat(route.getDrops().get(0).getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(route.getDrops().get(0).getDescription()).isEqualTo(routeRequest.getDescription());
        assertThat(route.getDrops().get(0).getEndTime()).isNotNull();
        assertThat(route.getDrops().get(0).getName()).isEqualTo(routeRequest.getName());
        assertThat(route.getDrops().get(0).getStartTime()).isNotNull();
        assertThat(route.getDrops().get(0).getUid()).startsWith(routeRequest.getName().substring(0, 6));
        assertThat(route.getDrops().get(0).getUid()).isNotEqualTo(routeRequest.getName().substring(0, 12));
        assertThat(route.getDrops().get(0).getUid()).hasSize(12);
        assertThat(route.getDrops().get(0).getSpot()).isEqualTo(spot);
        assertThat(route.getDrops().get(0).getRoute()).isEqualTo(route);
        assertThat(route.getDrops().get(0).getStatus()).isEqualTo(DropStatus.PREPARED);
        assertThat(prevDrop.getRoute()).isNull();
        assertThat(prevProduct.getRoute()).isNull();
    }

    @Test
    void givenRouteWhenToRouteResponseThenMap() {
        //given
        final Company company = Company.builder().build();
        final Route route = RouteDataGenerator.route(1, company);

        //when
        final RouteResponse response = routeMappingService.toRouteResponse(route);

        //then
        assertThat(response).isNotNull();
    }
}