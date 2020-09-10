package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropMembershipServiceTest {

    @InjectMocks
    private DropMembershipService dropMembershipService;

    @Mock
    private DropPersistenceService dropPersistenceService;

    @Mock
    private DropMappingService dropMappingService;

    @Mock
    private DropMembershipRepository dropMembershipRepository;

    @Mock
    private DropManagementValidationService dropManagementValidationService;

    @Test
    void givenDropWhenCreateDropMembershipThenCreate() {
        //given
        final DropJoinRequest dropJoinRequest = DropJoinRequest.builder()
                .password("aa")
                .build();
        final String dropUid = "dropUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Drop drop = DropDataGenerator.drop(1, null);
        final DropMembership membership = DropDataGenerator.membership(drop, customer);

        when(dropPersistenceService.findDrop(dropUid, companyUid)).thenReturn(drop);
        doNothing().when(dropManagementValidationService).validateJoinDropRequest(drop, dropJoinRequest, customer);
        when(dropMappingService.createMembership(drop, dropJoinRequest, accountAuthentication)).thenReturn(membership);
        when(dropMembershipRepository.save(membership)).thenReturn(membership);

        //when
        final ResourceOperationResponse result = dropMembershipService.createDropMembership(dropJoinRequest, dropUid, companyUid, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingDropWhenDeleteDropMembershipThenDelete() {
        //given
        final String dropUid = "dropUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Drop drop = DropDataGenerator.drop(1, null);
        final DropMembership membership = DropDataGenerator.membership(drop, customer);

        when(dropPersistenceService.findDrop(dropUid, companyUid)).thenReturn(drop);
        when(dropMembershipRepository.findByDropAndCustomer(drop, customer))
                .thenReturn(Optional.of(membership));
        doNothing().when(dropMembershipRepository).delete(membership);
        doNothing().when(dropManagementValidationService).validateDeleteDropMembership(membership);

        //when
        final ResourceOperationResponse result = dropMembershipService.deleteDropMembership(dropUid, companyUid, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }

    @Test
    void givenNotExistingDropWhenDeleteDropMembershipThenThrowException() {
        //given
        final String dropUid = "dropUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Drop drop = DropDataGenerator.drop(1, null);

        when(dropPersistenceService.findDrop(dropUid, companyUid)).thenReturn(drop);
        when(dropMembershipRepository.findByDropAndCustomer(drop, customer))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropMembershipService.deleteDropMembership(dropUid, companyUid, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenDropWhenDeleteMembershipsThenDelete() {
        //given
        final Drop drop = Drop.builder().build();

        doNothing().when(dropMembershipRepository).deleteByDrop(drop);

        //when
        dropMembershipService.deleteMemberships(drop);

        //then
        verifyNoMoreInteractions(dropMembershipRepository);
    }

    @Test
    void givenDropAndExistingMembershipAndRequestWhenUpdateMembershipThenUpdate() {
        //given
        final Drop drop = Drop.builder().build();
        final DropMembership dropMembership = DropMembership.builder().build();
        final Long dropMembershipId = 5L;
        final DropCompanyMembershipManagementRequest request = DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.BLOCKED.name()).build();

        when(dropMembershipRepository.findByIdAndDrop(dropMembershipId, drop)).thenReturn(Optional.of(dropMembership));
        when(dropMembershipRepository.save(dropMembership)).thenReturn(dropMembership);

        //when
        final ResourceOperationResponse result = dropMembershipService.updateMembership(drop, dropMembershipId, request);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);

        assertThat(dropMembership.getMembershipStatus()).isEqualTo(DropMembershipStatus.BLOCKED);
    }

    @Test
    void givenDropAndNotExistingMembershipAndRequestWhenUpdateMembershipThenError() {
        //given
        final Drop drop = Drop.builder().build();
        final Long dropMembershipId = 5L;
        final DropCompanyMembershipManagementRequest request = DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.BLOCKED.name()).build();

        when(dropMembershipRepository.findByIdAndDrop(dropMembershipId, drop)).thenReturn(Optional.empty());


        //when
        final Throwable throwable = catchThrowable(() -> dropMembershipService.updateMembership(drop, dropMembershipId, request));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingMembershipWhenExistsMembershipThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(dropMembershipRepository.existsByDropCompanyAndCustomerId(company, customerId)).thenReturn(true);

        //when
        final boolean result = dropMembershipService.existsMembership(company, customerId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenExistingMembershipWhenExistsMembershipThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(dropMembershipRepository.existsByDropCompanyAndCustomerId(company, customerId)).thenReturn(false);

        //when
        final boolean result = dropMembershipService.existsMembership(company, customerId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenExistingDropWhenUpdateDropMembershipThenUpdate() {
        //given
        final String dropUid = "dropUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Drop drop = DropDataGenerator.drop(1, null);
        final DropMembership membership = DropDataGenerator.membership(drop, customer);

        when(dropPersistenceService.findDrop(dropUid, companyUid)).thenReturn(drop);
        when(dropMembershipRepository.findByDropAndCustomer(drop, customer))
                .thenReturn(Optional.of(membership));
        when(dropMembershipRepository.save(membership)).thenReturn(membership);
        final DropMembershipManagementRequest dropMembershipManagementRequest = DropMembershipManagementRequest.builder()
                .receiveNotification(true)
                .build();

        //when
        final ResourceOperationResponse result = dropMembershipService.updateDropMembership(dropMembershipManagementRequest, dropUid, companyUid, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(membership.isReceiveNotification()).isTrue();
    }

    @Test
    void givenNotExistingDropWhenUpdateDropMembershipThenThrowException() {
        //given
        final String dropUid = "dropUid";
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .customer(customer)
                .build();
        final Drop drop = DropDataGenerator.drop(1, null);

        when(dropPersistenceService.findDrop(dropUid, companyUid)).thenReturn(drop);
        when(dropMembershipRepository.findByDropAndCustomer(drop, customer))
                .thenReturn(Optional.empty());
        final DropMembershipManagementRequest dropMembershipManagementRequest = DropMembershipManagementRequest.builder()
                .receiveNotification(true)
                .build();
        //when
        final Throwable throwable = catchThrowable(() -> dropMembershipService.updateDropMembership(dropMembershipManagementRequest, dropUid, companyUid, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }
}