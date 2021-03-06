package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.service.NotificationService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.SpotMembershipNotificationStatus;
import com.drop.here.backend.drophere.spot.dto.request.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotMembershipServiceTest {

    @InjectMocks
    private SpotMembershipService spotMembershipService;

    @Mock
    private SpotPersistenceService spotPersistenceService;

    @Mock
    private SpotMappingService spotMappingService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SpotMembershipRepository spotMembershipRepository;

    @Mock
    private SpotManagementValidationService spotManagementValidationService;

    @Test
    void givenSpotWhenCreateSpotMembershipThenCreate() {
        //given
        final SpotJoinRequest spotJoinRequest = SpotJoinRequest.builder()
                .password("aa")
                .build();
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Spot spot = SpotDataGenerator.spot(1, null);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);

        when(spotPersistenceService.findSpot(spotUid, companyUid)).thenReturn(spot);
        doNothing().when(spotManagementValidationService).validateJoinSpotRequest(spot, spotJoinRequest, customer);
        when(spotMappingService.createMembership(spot, spotJoinRequest, accountAuthentication)).thenReturn(membership);
        when(spotMembershipRepository.save(membership)).thenReturn(membership);

        //when
        final ResourceOperationResponse result = spotMembershipService.createSpotMembership(spotJoinRequest, spotUid, companyUid, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingSpotWhenDeleteSpotMembershipThenDelete() {
        //given
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Spot spot = SpotDataGenerator.spot(1, null);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);

        when(spotPersistenceService.findSpot(spotUid, companyUid)).thenReturn(spot);
        when(spotMembershipRepository.findBySpotAndCustomer(spot, customer))
                .thenReturn(Optional.of(membership));
        doNothing().when(spotMembershipRepository).delete(membership);
        doNothing().when(spotManagementValidationService).validateDeleteSpotMembership(membership);

        //when
        final ResourceOperationResponse result = spotMembershipService.deleteSpotMembership(spotUid, companyUid, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }

    @Test
    void givenNotExistingSpotWhenDeleteSpotMembershipThenThrowException() {
        //given
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Spot spot = SpotDataGenerator.spot(1, null);

        when(spotPersistenceService.findSpot(spotUid, companyUid)).thenReturn(spot);
        when(spotMembershipRepository.findBySpotAndCustomer(spot, customer))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> spotMembershipService.deleteSpotMembership(spotUid, companyUid, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenSpotWhenDeleteMembershipsThenDelete() {
        //given
        final Spot spot = Spot.builder().build();

        doNothing().when(spotMembershipRepository).deleteBySpot(spot);

        //when
        spotMembershipService.deleteMemberships(spot);

        //then
        verifyNoMoreInteractions(spotMembershipRepository);
    }

    @Test
    void givenSpotAndExistingMembershipAndRequestWhenUpdateMembershipThenUpdate() {
        //given
        final Spot spot = Spot.builder().build();
        final SpotMembership spotMembership = SpotMembership.builder().build();
        final Long spotMembershipId = 5L;
        final SpotCompanyMembershipManagementRequest request = SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.BLOCKED.name()).build();

        when(spotMembershipRepository.findByIdAndSpot(spotMembershipId, spot)).thenReturn(Optional.of(spotMembership));
        when(spotMembershipRepository.save(spotMembership)).thenReturn(spotMembership);

        //when
        final ResourceOperationResponse result = spotMembershipService.updateMembership(spot, spotMembershipId, request);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);

        assertThat(spotMembership.getMembershipStatus()).isEqualTo(SpotMembershipStatus.BLOCKED);
        verifyNoInteractions(notificationService);
    }

    @Test
    void givenSpotAndExistingMembershipAndRequestFromPendingToActiveWhenUpdateMembershipThenUpdateAndNotify() {
        //given
        final Spot spot = Spot.builder().build();
        final SpotMembership spotMembership = SpotMembership.builder().customer(Customer.builder().build()).membershipStatus(SpotMembershipStatus.PENDING).build();
        final Long spotMembershipId = 5L;
        final SpotCompanyMembershipManagementRequest request = SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.ACTIVE.name()).build();

        when(spotMembershipRepository.findByIdAndSpot(spotMembershipId, spot)).thenReturn(Optional.of(spotMembership));
        when(spotMembershipRepository.save(spotMembership)).thenReturn(spotMembership);
        doNothing().when(notificationService).createNotifications(any());

        //when
        final ResourceOperationResponse result = spotMembershipService.updateMembership(spot, spotMembershipId, request);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(spotMembership.getMembershipStatus()).isEqualTo(SpotMembershipStatus.ACTIVE);
    }

    @Test
    void givenSpotAndNotExistingMembershipAndRequestWhenUpdateMembershipThenError() {
        //given
        final Spot spot = Spot.builder().build();
        final Long spotMembershipId = 5L;
        final SpotCompanyMembershipManagementRequest request = SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.BLOCKED.name()).build();

        when(spotMembershipRepository.findByIdAndSpot(spotMembershipId, spot)).thenReturn(Optional.empty());


        //when
        final Throwable throwable = catchThrowable(() -> spotMembershipService.updateMembership(spot, spotMembershipId, request));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingMembershipWhenExistsMembershipThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(spotMembershipRepository.existsBySpotCompanyAndCustomerId(company, customerId)).thenReturn(true);

        //when
        final boolean result = spotMembershipService.existsMembership(company, customerId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenExistingMembershipWhenExistsMembershipThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(spotMembershipRepository.existsBySpotCompanyAndCustomerId(company, customerId)).thenReturn(false);

        //when
        final boolean result = spotMembershipService.existsMembership(company, customerId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenExistingSpotWhenUpdateSpotMembershipThenUpdate() {
        //given
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Spot spot = SpotDataGenerator.spot(1, null);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);

        when(spotPersistenceService.findSpot(spotUid, companyUid)).thenReturn(spot);
        when(spotMembershipRepository.findBySpotAndCustomer(spot, customer))
                .thenReturn(Optional.of(membership));
        when(spotMembershipRepository.save(membership)).thenReturn(membership);
        final SpotMembershipManagementRequest spotMembershipManagementRequest = SpotMembershipManagementRequest.builder().build();
        doNothing().when(spotMappingService).updateSpotMembership(membership, spotMembershipManagementRequest);

        //when
        final ResourceOperationResponse result = spotMembershipService.updateSpotMembership(spotMembershipManagementRequest, spotUid, companyUid, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenNotExistingSpotWhenUpdateSpotMembershipThenThrowException() {
        //given
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Spot spot = SpotDataGenerator.spot(1, null);

        when(spotPersistenceService.findSpot(spotUid, companyUid)).thenReturn(spot);
        when(spotMembershipRepository.findBySpotAndCustomer(spot, customer))
                .thenReturn(Optional.empty());
        final SpotMembershipManagementRequest spotMembershipManagementRequest = SpotMembershipManagementRequest.builder()
                .build();
        //when
        final Throwable throwable = catchThrowable(() -> spotMembershipService.updateSpotMembership(spotMembershipManagementRequest, spotUid, companyUid, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenSpotAndDelayedNotificationStatusWhenFindToBeNotifiedThenFind() {
        //given
        final Spot spot = Spot.builder().build();
        final SpotMembershipNotificationStatus spotMembershipNotificationStatus =
                SpotMembershipNotificationStatus.delayed();

        final List<SpotMembership> spotMemberships = List.of(SpotMembership.builder().build());

        when(spotMembershipRepository.findToBeNotified(spot, false, false, false, true, false)).thenReturn(spotMemberships);

        //when
        final List<SpotMembership> result = spotMembershipService.findToBeNotified(spot, spotMembershipNotificationStatus);

        //then
        assertThat(result).isEqualTo(spotMemberships);
    }
}