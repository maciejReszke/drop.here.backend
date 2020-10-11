package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfilePersistenceService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.dto.RouteStateChangeRequest;
import com.drop.here.backend.drophere.route.dto.UnpreparedRouteRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.service.state_update.RouteUpdateStateServiceFactory;
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

import java.time.LocalDateTime;
import java.util.List;
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
    private RoutePersistenceService routePersistenceService;

    @Mock
    private RouteValidationService routeValidationService;

    @Mock
    private AccountProfilePersistenceService accountProfilePersistenceService;

    @Mock
    private RouteUpdateStateServiceFactory routeUpdateStateServiceFactory;

    @Test
    void givenRequestWhenCreateRouteThenCreate() {
        //given
        final String companyUid = "companyUid";
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Route route = RouteDataGenerator.route(1, company);

        doNothing().when(routeValidationService).validateCreate(routeRequest);
        when(routeMappingService.toRoute(routeRequest, company)).thenReturn(route);
        doNothing().when(routePersistenceService).save(route);

        //when
        final ResourceOperationResponse result = routeService.createRoute(companyUid, routeRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingRouteUnpreparedRouteRequestWhenUpdateRouteThenUpdate() {
        //given
        final String companyUid = "companyUid";
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;
        final Route route = RouteDataGenerator.route(1, company);

        doNothing().when(routeValidationService).validateUpdateUnprepared(routeRequest, route);
        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
        doNothing().when(routeMappingService).updateRoute(route, routeRequest, company);
        doNothing().when(routePersistenceService).save(route);

        //when
        final ResourceOperationResponse result = routeService.updateUnpreparedRoute(companyUid, routeId, routeRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenExistingRouteStateChangedRouteRequestWithChangedSellerProfileWhenUpdateRouteThenUpdate() {
        //given
        final String companyUid = "companyUid";
        final RouteStateChangeRequest routeRequest = RouteDataGenerator.stateChangeRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;
        final Route route = RouteDataGenerator.route(1, company);
        route.setWithSeller(false);
        route.setStatus(null);

        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
        doNothing().when(routeValidationService).validateUpdateStateChanged(route, accountAuthentication.getProfile());
        when(accountProfilePersistenceService.findActiveByCompanyAndProfileUid(company, routeRequest.getChangedProfileUid()))
                .thenReturn(Optional.of(accountProfile));
        doNothing().when(routePersistenceService).save(route);
        when(routeUpdateStateServiceFactory.update(route, routeRequest)).thenReturn(RouteStatus.FINISHED);

        //when
        final ResourceOperationResponse result = routeService.updateRouteStatus(companyUid, routeId, routeRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(route.getStatus()).isEqualTo(RouteStatus.FINISHED);
        assertThat(route.getProfile()).isEqualTo(accountProfile);
        assertThat(route.isWithSeller()).isTrue();
    }

    @Test
    void givenNotExistingRouteWhenUpdateStateRouteThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final RouteStateChangeRequest routeRequest = RouteDataGenerator.stateChangeRequest(1);
        routeRequest.setChangedProfileUid(null);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;
        final Route route = RouteDataGenerator.route(1, company);
        route.setWithSeller(false);
        route.setStatus(null);

        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
        doNothing().when(routeValidationService).validateUpdateStateChanged(route, accountAuthentication.getProfile());
        doNothing().when(routePersistenceService).save(route);
        when(routeUpdateStateServiceFactory.update(route, routeRequest)).thenReturn(RouteStatus.FINISHED);

        //when
        final ResourceOperationResponse result = routeService.updateRouteStatus(companyUid, routeId, routeRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(route.getStatus()).isEqualTo(RouteStatus.FINISHED);
        assertThat(route.getProfile()).isNull();
        assertThat(route.isWithSeller()).isFalse();
    }

    @Test
    void givenNotExistingRouteWhenUpdateUnpreparedRouteThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;

        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> routeService.updateUnpreparedRoute(companyUid, routeId, routeRequest, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenNotExistingRouteWhenUpdateRouteThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final UnpreparedRouteRequest routeRequest = RouteDataGenerator.unprepared(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long routeId = 15L;

        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> routeService.updateUnpreparedRoute(companyUid, routeId, routeRequest, accountAuthentication));

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
        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
        doNothing().when(routePersistenceService).delete(route);

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

        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.empty());

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

        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.of(route));
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

        when(routePersistenceService.findByIdAndCompany(routeId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> routeService.findRoute(routeId, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenNotFinishedRouteWhenFinishToBeFinishedThenUpdateStatus() {
        //given
        final Route route = Route.builder().build();

        when(routePersistenceService.finishToBeFinished()).thenReturn(List.of(route));
        doNothing().when(routePersistenceService).save(route);

        //when
        routeService.finishToBeFinished();

        //then
        assertThat(route.getStatus()).isEqualTo(RouteStatus.FINISHED);
        assertThat(route.getFinishedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }
}