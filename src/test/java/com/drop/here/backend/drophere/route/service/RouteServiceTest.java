package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {
    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteMappingService routeMappingService;

    @Mock
    private RouteStoreService routeStoreService;

    @Mock
    private RouteValidationService routeValidationService;

    @Test
    void givenRequestWhenCreateRouteThenCreate() {
        //given
        final String companyUid = "companyUid";
        final RouteRequest routeRequest = RouteDataGenerator.request(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Route route = RouteDataGenerator.route(1, company);

        doNothing().when(routeValidationService).validateCreate(routeRequest);
        when(routeMappingService.toRoute(routeRequest, company)).thenReturn(route);
        doNothing().when(routeStoreService).save(route);

        //when
        final ResourceOperationResponse result = routeService.createRoute(companyUid, routeRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingRouteWhenUpdateRouteThenUpdate() {
        //given
        final String companyUid = "companyUid";
        final RouteRequest routeRequest = RouteDataGenerator.request(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;
        final Route route = RouteDataGenerator.route(1, company);

        doNothing().when(routeValidationService).validateCreate(routeRequest);
        when(routeStoreService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
        doNothing().when(routeMappingService).updateRoute(route, routeRequest, company);
        doNothing().when(routeStoreService).save(route);

        //when
        final ResourceOperationResponse result = routeService.updateRoute(companyUid, routeId, routeRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenNotExistingRouteWhenUpdateRouteThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final RouteRequest routeRequest = RouteDataGenerator.request(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;

        doNothing().when(routeValidationService).validateCreate(routeRequest);
        when(routeStoreService.findByIdAndCompany(routeId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> routeService.updateRoute(companyUid, routeId, routeRequest, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingRouteWhenDeleteRouteThenDelete() {
        //given
        final String companyUid = "companyUid";
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;
        final Route route = RouteDataGenerator.route(1, company);

        doNothing().when(routeValidationService).validateDelete(route);
        when(routeStoreService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
        doNothing().when(routeStoreService).delete(route);

        //when
        final ResourceOperationResponse result = routeService.deleteRoute(companyUid, routeId, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }

    @Test
    void givenNotExistingRouteWhenDeleteRouteThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;

        when(routeStoreService.findByIdAndCompany(routeId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> routeService.deleteRoute(companyUid, routeId, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingRouteWhenFindRouteThenFind() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;
        final RouteResponse routeResponse = RouteResponse.builder().build();
        final Route route = RouteDataGenerator.route(1, company);

        when(routeStoreService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
        when(routeMappingService.toRouteResponse(route)).thenReturn(routeResponse);

        //when
        final RouteResponse result = routeService.findRoute(routeId, accountAuthentication);

        //then
        assertThat(result).isEqualTo(routeResponse);
    }

    @Test
    void givenNotExistingRouteWhenFindRouteThenThrowException() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;

        when(routeStoreService.findByIdAndCompany(routeId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> routeService.findRoute(routeId, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

}