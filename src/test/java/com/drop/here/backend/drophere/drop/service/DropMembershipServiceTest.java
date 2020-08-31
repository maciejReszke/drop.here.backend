package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropMembershipResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropMembershipServiceTest {

    @InjectMocks
    private DropMembershipService dropMembershipService;

    @Mock
    private DropManagementService dropManagementService;

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
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();
        final Drop drop = DropDataGenerator.drop(1, null);
        final DropMembership membership = DropDataGenerator.membership(1);

        when(dropManagementService.findDrop(dropUid, companyUid)).thenReturn(drop);
        doNothing().when(dropManagementValidationService).validateCreatingDropMembershipRequest(drop, dropJoinRequest);
        when(dropMappingService.createMembership(drop, accountAuthentication)).thenReturn(membership);
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
        final DropMembership membership = DropDataGenerator.membership(1);

        when(dropManagementService.findDrop(dropUid, companyUid)).thenReturn(drop);
        when(dropMembershipRepository.findByDropAndCustomer(drop, customer))
                .thenReturn(Optional.of(membership));
        doNothing().when(dropMembershipRepository).delete(membership);

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

        when(dropManagementService.findDrop(dropUid, companyUid)).thenReturn(drop);
        when(dropMembershipRepository.findByDropAndCustomer(drop, customer))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropMembershipService.deleteDropMembership(dropUid, companyUid, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenAuthenticationAndNameWhenFindMembershipsThenFind() {
        //given
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication
                .builder()
                .customer(customer)
                .build();
        final String name = "name";
        final Pageable pageable = Pageable.unpaged();
        final DropMembershipResponse dropMembershipResponse = DropMembershipResponse.builder().build();

        final DropMembership membership = DropDataGenerator.membership(1);
        when(dropMembershipRepository.findByCustomerAndDropNameStartsWith(customer, name, pageable))
                .thenReturn(new PageImpl<>(List.of(membership)));
        when(dropMappingService.toDropMembershipResponse(membership)).thenReturn(dropMembershipResponse);

        //when
        final Page<DropMembershipResponse> response = dropMembershipService.findMemberships(accountAuthentication, name, pageable);

        //then
        assertThat(response.get()).hasSize(1);
        assertThat(response.get().findFirst().orElseThrow()).isEqualTo(dropMembershipResponse);
    }
}