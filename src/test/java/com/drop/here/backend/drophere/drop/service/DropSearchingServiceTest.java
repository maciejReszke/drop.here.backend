package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropProductResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropSearchingServiceTest {

    @InjectMocks
    private DropSearchingService dropSearchingService;

    @Mock
    private DropRepository dropRepository;

    @Mock
    private RouteProductRepository routeProductRepository;

    @Test
    void givenProductsAndCompanyWhenFindProductDropsThenFind() {
        //given
        final List<Long> productIds = List.of(5L);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Route route = Route.builder().id(5L).build();
        final RouteProduct product = RouteProduct.builder().route(route).originalProduct(Product.builder().build()).build();
        final Drop drop = Drop.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).route(route)
                .spot(Spot.builder().build())
                .build();

        when(routeProductRepository.findByOriginalProductIdIn(productIds)).thenReturn(List.of(product));
        when(dropRepository.findUpcomingByRouteIdInWithSpotForCompany(Set.of(5L)))
                .thenReturn(List.of(drop));

        //when
        final List<DropProductResponse> result = dropSearchingService.findProductDrops(productIds, accountAuthentication);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void givenProductsAndCustomerWhenFindProductDropsThenFind() {
        //given
        final List<Long> productIds = List.of(5L);
        final Customer customer = Customer.builder().build();
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setCustomer(customer);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Route route = Route.builder().id(5L).build();
        final RouteProduct product = RouteProduct.builder().route(route).originalProduct(Product.builder().build()).build();
        final Drop drop = Drop.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).route(route)
                .spot(Spot.builder().build())
                .build();

        when(routeProductRepository.findByOriginalProductIdIn(productIds)).thenReturn(List.of(product));
        when(dropRepository.findUpcomingByRouteIdInWithSpotForCustomer(Set.of(5L), customer))
                .thenReturn(List.of(drop));

        //when
        final List<DropProductResponse> result = dropSearchingService.findProductDrops(productIds, accountAuthentication);

        //then
        assertThat(result).isNotNull();
    }
}