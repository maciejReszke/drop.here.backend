package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropManagementValidationServiceTest {

    @InjectMocks
    private DropManagementValidationService dropManagementValidationService;

    @Mock
    private DropMembershipRepository dropMembershipRepository;

    @Test
    void givenValidWithPasswordRequestWhenValidateDropRequestThenDoNothing() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .xCoordinate(1D)
                .yCoordinate(1D)
                .estimatedRadiusMeters(100)
                .password("a")
                .requiresPassword(true)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDropRequest(dropManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidWithoutPasswordRequestWhenValidateDropRequestThenDoNothing() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .xCoordinate(1D)
                .yCoordinate(1D)
                .estimatedRadiusMeters(100)
                .password(null)
                .requiresPassword(false)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDropRequest(dropManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLackOfPasswordWhenWithPasswordWhenValidateDropRequestThenError() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .password(null)
                .requiresPassword(true)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDropRequest(dropManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class)
                .matches(t -> ((RestIllegalRequestValueException) (t)).getCode() ==
                        RestExceptionStatusCode.DROP_WITH_PASSWORD_REQUIREMENT_LACK_OF_PASSWORD.ordinal());
    }

    @Test
    void givenDropWithoutPasswordAndValidJoinDropRequestThenDoNothing() {
        //given
        final Drop drop = Drop.builder()
                .requiresPassword(false)
                .build();

        final Customer customer = Customer.builder().build();
        final DropJoinRequest dropJoinRequest = DropJoinRequest.builder().build();

        when(dropMembershipRepository.findByDropAndCustomer(drop, customer)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateJoinDropRequest(drop, dropJoinRequest, customer));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDropWithValidPasswordAndValidJoinDropRequestThenDoNothing() {
        //given
        final Drop drop = Drop.builder()
                .requiresPassword(true)
                .password("pass123")
                .build();

        final Customer customer = Customer.builder().build();
        final DropJoinRequest dropJoinRequest = DropJoinRequest.builder()
                .password("pass123")
                .build();

        when(dropMembershipRepository.findByDropAndCustomer(drop, customer)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateJoinDropRequest(drop, dropJoinRequest, customer));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDropWithInvalidPasswordAndValidJoinDropRequestThenThrowException() {
        //given
        final Drop drop = Drop.builder()
                .requiresPassword(true)
                .password("pass123")
                .build();

        final Customer customer = Customer.builder().build();
        final DropJoinRequest dropJoinRequest = DropJoinRequest.builder()
                .password("pass321")
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateJoinDropRequest(drop, dropJoinRequest, customer));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }


    @Test
    void givenDropDuplicateWhenValidateJoinDropRequestThenThrowException() {
        //given
        final Drop drop = Drop.builder()
                .requiresPassword(false)
                .build();

        final Customer customer = Customer.builder().build();
        final DropJoinRequest dropJoinRequest = DropJoinRequest.builder()
                .build();

        when(dropMembershipRepository.findByDropAndCustomer(drop, customer))
                .thenReturn(Optional.of(DropMembership.builder().build()));

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateJoinDropRequest(drop, dropJoinRequest, customer));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidMembershipStatusWhenValidateUpdateMembershipThenThrowException() {
        //given
        final DropCompanyMembershipManagementRequest request = DropCompanyMembershipManagementRequest.builder().membershipStatus("aa").build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPendingMembershipStatusWhenValidateUpdateMembershipThenThrowException() {
        //given
        final DropCompanyMembershipManagementRequest request = DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.PENDING.name()).build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenBlockedMembershipStatusWhenValidateUpdateMembershipThenDoNothing() {
        //given
        final DropCompanyMembershipManagementRequest request = DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.BLOCKED.name()).build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenActiveMembershipStatusWhenValidateUpdateMembershipThenDoNothing() {
        //given
        final DropCompanyMembershipManagementRequest request = DropCompanyMembershipManagementRequest.builder()
                .membershipStatus(DropMembershipStatus.ACTIVE.name()).build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidDropMembershipWhenValidateDeleteDropMembershipThenDoNothing() {
        //given
        final DropMembership membership = DropMembership.builder().membershipStatus(DropMembershipStatus.ACTIVE).build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDeleteDropMembership(membership));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenBlockedDropMembershipWhenValidateDeleteDropMembershipThenThrowException() {
        //given
        final DropMembership membership = DropMembership.builder().membershipStatus(DropMembershipStatus.BLOCKED).build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDeleteDropMembership(membership));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }
}