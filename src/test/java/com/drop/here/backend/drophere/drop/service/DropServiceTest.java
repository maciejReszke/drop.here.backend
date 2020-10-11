package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfilePersistenceService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropDetailedCustomerResponse;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.drop.service.update.DropUpdateServiceFactory;
import com.drop.here.backend.drophere.route.dto.RouteProductRouteResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.service.RouteProductMappingService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.response.SpotBaseCustomerResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.service.SpotMappingService;
import com.drop.here.backend.drophere.spot.service.SpotPersistenceService;
import com.drop.here.backend.drophere.spot.service.SpotSearchingService;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropServiceTest {

    @InjectMocks
    private DropService dropService;

    @Mock
    private DropRepository dropRepository;

    @Mock
    private SpotMappingService spotMappingService;

    @Mock
    private SpotPersistenceService spotPersistenceService;

    @Mock
    private AccountProfilePersistenceService accountProfilePersistenceService;

    @Mock
    private RouteProductMappingService routeProductMappingService;

    @Mock
    private DropValidationService dropValidationService;

    @Mock
    private DropUpdateServiceFactory dropUpdateServiceFactory;

    @Mock
    private SpotSearchingService spotSearchingService;

    @Test
    void givenRouteWhenToDropResponsesThenMap() {
        //given
        final Route route = Route.builder().build();

        final Spot spot = Spot.builder().build();
        final Drop drop = DropDataGenerator.drop(1, null, spot);
        final SpotCompanyResponse spotResponse = SpotCompanyResponse.builder().build();
        when(dropRepository.findByRouteWithSpot(route)).thenReturn(List.of(drop));
        when(spotMappingService.toSpotCompanyResponse(spot)).thenReturn(spotResponse);

        //when
        final List<DropRouteResponse> response = dropService.toDropRouteResponses(route);

        //then
        final DropRouteResponse dropResponse = response.get(0);
        assertThat(dropResponse.getDescription()).isEqualTo(drop.getDescription());
        assertThat(dropResponse.getEndTime()).isEqualTo(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getName()).isEqualTo(drop.getName());
        assertThat(dropResponse.getStartTime()).isEqualTo(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getSpot()).isEqualTo(spotResponse);
        assertThat(dropResponse.getStatus()).isEqualTo(drop.getStatus());
        assertThat(dropResponse.getUid()).isEqualTo(drop.getUid());
    }

    @Test
    void givenExistingPrivilegedDropAndAccountProfileWhenFindDropForCustomerThenFind() {
        //given
        final String dropUid = "dropUid";
        final Customer customer = Customer.builder().build();
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setCustomer(customer);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        final Spot spot = Spot.builder().id(5L).build();
        final Drop drop = Drop.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).spot(spot).build();
        final AccountProfile accountProfile = AccountProfile.builder().firstName("f").lastName("l").profileUid("u").build();
        final SpotBaseCustomerResponse spotBaseCustomerResponse = SpotBaseCustomerResponse.builder().build();
        final List<RouteProductRouteResponse> routeProductRouteRespons = List.of();

        when(dropRepository.findPrivilegedDrop(dropUid, customer)).thenReturn(Optional.of(drop));
        when(spotPersistenceService.findByIdWithCompany(5L)).thenReturn(spot);
        when(accountProfilePersistenceService.findByDrop(drop)).thenReturn(Optional.of(accountProfile));
        when(spotSearchingService.findSpot(spot, customer)).thenReturn(spotBaseCustomerResponse);
        when(routeProductMappingService.toProductResponses(drop)).thenReturn(routeProductRouteRespons);
        when(dropRepository.isSellerLocationAvailableForCustomer(accountProfile.getProfileUid(), customer))
                .thenReturn(true);

        //when
        final DropDetailedCustomerResponse dropResponse = dropService.findDropForCustomer(dropUid, accountAuthentication);

        //then
        assertThat(dropResponse.getDescription()).isEqualTo(drop.getDescription());
        assertThat(dropResponse.getEndTime()).isEqualTo(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getName()).isEqualTo(drop.getName());
        assertThat(dropResponse.getStartTime()).isEqualTo(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getSpot()).isEqualTo(spotBaseCustomerResponse);
        assertThat(dropResponse.getProducts()).isEqualTo(routeProductRouteRespons);
        assertThat(dropResponse.getStatus()).isEqualTo(drop.getStatus());
        assertThat(dropResponse.getUid()).isEqualTo(drop.getUid());
        assertThat(dropResponse.getProfileFirstName()).isEqualTo(accountProfile.getFirstName());
        assertThat(dropResponse.getProfileLastName()).isEqualTo(accountProfile.getLastName());
        assertThat(dropResponse.getProfileUid()).isEqualTo(accountProfile.getProfileUid());
        assertThat(dropResponse.isStreamingPosition()).isTrue();
    }

    @Test
    void givenExistingPrivilegedDropWithoutAccountProfileWhenFindDropForCustomerThenFind() {
        //given
        final String dropUid = "dropUid";
        final Customer customer = Customer.builder().build();
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setCustomer(customer);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        final Spot spot = Spot.builder().id(5L).build();
        final Drop drop = Drop.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).spot(spot).build();
        final SpotBaseCustomerResponse spotBaseCustomerResponse = SpotBaseCustomerResponse.builder().build();
        final List<RouteProductRouteResponse> routeProductRouteRespons = List.of();

        when(dropRepository.findPrivilegedDrop(dropUid, customer)).thenReturn(Optional.of(drop));
        when(spotPersistenceService.findByIdWithCompany(5L)).thenReturn(spot);
        when(accountProfilePersistenceService.findByDrop(drop)).thenReturn(Optional.empty());
        when(spotSearchingService.findSpot(spot, customer)).thenReturn(spotBaseCustomerResponse);
        when(routeProductMappingService.toProductResponses(drop)).thenReturn(routeProductRouteRespons);

        //when
        final DropDetailedCustomerResponse dropResponse = dropService.findDropForCustomer(dropUid, accountAuthentication);

        //then
        assertThat(dropResponse.getDescription()).isEqualTo(drop.getDescription());
        assertThat(dropResponse.getEndTime()).isEqualTo(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getName()).isEqualTo(drop.getName());
        assertThat(dropResponse.getStartTime()).isEqualTo(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getSpot()).isEqualTo(spotBaseCustomerResponse);
        assertThat(dropResponse.getProducts()).isEqualTo(routeProductRouteRespons);
        assertThat(dropResponse.getStatus()).isEqualTo(drop.getStatus());
        assertThat(dropResponse.getUid()).isEqualTo(drop.getUid());
        assertThat(dropResponse.getProfileFirstName()).isNull();
        assertThat(dropResponse.getProfileLastName()).isNull();
        assertThat(dropResponse.getProfileUid()).isNull();
        assertThat(dropResponse.isStreamingPosition()).isFalse();
    }

    @Test
    void givenExistingDropWhenUpdateDropThenUpdate() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder().build();
        final String dropUid = "dropUid";
        final Company company = Company.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .company(company)
                .profile(accountProfile)
                .build();
        final Drop drop = Drop.builder().build();

        when(dropRepository.findByUidAndRouteCompanyWithSpot(dropUid, company)).thenReturn(Optional.of(drop));
        doNothing().when(dropValidationService).validateUpdate(drop, accountProfile);
        when(dropUpdateServiceFactory.update(drop, drop.getSpot(), company, accountProfile, dropManagementRequest)).thenReturn(DropStatus.DELAYED);
        when(dropRepository.save(drop)).thenReturn(drop);
        //when
        final ResourceOperationResponse result = dropService.updateDrop(dropManagementRequest, dropUid, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(drop.getStatus()).isEqualTo(DropStatus.DELAYED);
    }

    @Test
    void givenNotExistingDropWhenUpdateDropThenThrowException() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder().build();
        final String dropUid = "dropUid";
        final Company company = Company.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .company(company)
                .profile(accountProfile)
                .build();
        when(dropRepository.findByUidAndRouteCompanyWithSpot(dropUid, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropService.updateDrop(dropManagementRequest, dropUid, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingDropWhenPrepareDropsThenUpdateStatus() {
        //given
        final Company company = Company.builder().build();
        final Route route = RouteDataGenerator.route(1, company);
        final Spot spot = Spot.builder().build();
        final Drop drop = Drop.builder().spot(spot).build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        route.setProfile(accountProfile);

        when(dropRepository.findByRouteWithSpot(route)).thenReturn(List.of(drop));
        when(dropUpdateServiceFactory.prepare(drop, spot, company, accountProfile))
                .thenReturn(DropStatus.CANCELLED);
        when(dropRepository.save(drop)).thenReturn(drop);

        //when
        dropService.prepareDrops(route);

        //then
        assertThat(drop.getStatus()).isEqualTo(DropStatus.CANCELLED);
    }

    @Test
    void givenNotCanceledNorFinishedDropWhenCancelDropsThenUpdateStatus() {
        //given
        final Company company = Company.builder().build();
        final Route route = RouteDataGenerator.route(1, company);
        final Spot spot = Spot.builder().build();
        final Drop drop = Drop.builder().spot(spot).status(DropStatus.DELAYED).build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        route.setProfile(accountProfile);

        when(dropRepository.findByRouteWithSpot(route)).thenReturn(List.of(drop));
        when(dropUpdateServiceFactory.update(eq(drop), eq(spot), eq(company), eq(accountProfile), any()))
                .thenReturn(DropStatus.CANCELLED);
        when(dropRepository.save(drop)).thenReturn(drop);

        //when
        dropService.cancelDrops(route);

        //then
        assertThat(drop.getStatus()).isEqualTo(DropStatus.CANCELLED);
    }

    @Test
    void givenCancelledDropWhenCancelDropsThenDoNothing() {
        //given
        final Company company = Company.builder().build();
        final Route route = RouteDataGenerator.route(1, company);
        final Spot spot = Spot.builder().build();
        final Drop drop = Drop.builder().spot(spot).status(DropStatus.CANCELLED).build();

        when(dropRepository.findByRouteWithSpot(route)).thenReturn(List.of(drop));

        //when
        dropService.cancelDrops(route);

        //then
        assertThat(drop.getStatus()).isEqualTo(DropStatus.CANCELLED);
    }

    @Test
    void givenFinishedDropWhenCancelDropsThenDoNothing() {
        //given
        final Company company = Company.builder().build();
        final Route route = RouteDataGenerator.route(1, company);
        final Spot spot = Spot.builder().build();
        final Drop drop = Drop.builder().spot(spot).status(DropStatus.FINISHED).build();

        when(dropRepository.findByRouteWithSpot(route)).thenReturn(List.of(drop));

        //when
        dropService.cancelDrops(route);

        //then
        assertThat(drop.getStatus()).isEqualTo(DropStatus.FINISHED);
    }
}