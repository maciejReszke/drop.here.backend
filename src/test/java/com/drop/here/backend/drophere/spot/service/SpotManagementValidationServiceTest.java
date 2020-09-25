package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotManagementValidationServiceTest {

    @InjectMocks
    private SpotManagementValidationService spotManagementValidationService;

    @Mock
    private SpotMembershipRepository spotMembershipRepository;

    @Test
    void givenValidWithPasswordRequestWhenValidateSpotRequestThenDoNothing() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder()
                .xCoordinate(1D)
                .yCoordinate(1D)
                .estimatedRadiusMeters(100)
                .password("a")
                .requiresPassword(true)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateSpotRequest(spotManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidWithoutPasswordRequestWhenValidateSpotRequestThenDoNothing() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder()
                .xCoordinate(1D)
                .yCoordinate(1D)
                .estimatedRadiusMeters(100)
                .password(null)
                .requiresPassword(false)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateSpotRequest(spotManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLackOfPasswordWhenWithPasswordWhenValidateSpotRequestThenError() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder()
                .password(null)
                .requiresPassword(true)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateSpotRequest(spotManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class)
                .matches(t -> ((RestIllegalRequestValueException) (t)).getCode() ==
                        RestExceptionStatusCode.SPOT_WITH_PASSWORD_REQUIREMENT_LACK_OF_PASSWORD.ordinal());
    }

    @Test
    void givenSpotWithoutPasswordAndValidJoinSpotRequestThenDoNothing() {
        //given
        final Spot spot = Spot.builder()
                .requiresPassword(false)
                .build();

        final Customer customer = Customer.builder().build();
        final SpotJoinRequest spotJoinRequest = SpotJoinRequest.builder().build();

        when(spotMembershipRepository.findBySpotAndCustomer(spot, customer)).thenReturn(Mono.empty());

        //when
        final Mono<Void> result = spotManagementValidationService.validateJoinSpotRequest(spot, spotJoinRequest, customer);

        //then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void givenSpotWithValidPasswordAndValidJoinSpotRequestThenDoNothing() {
        //given
        final Spot spot = Spot.builder()
                .requiresPassword(true)
                .password("pass123")
                .build();

        final Customer customer = Customer.builder().build();
        final SpotJoinRequest spotJoinRequest = SpotJoinRequest.builder()
                .password("pass123")
                .build();

        when(spotMembershipRepository.findBySpotAndCustomer(spot, customer)).thenReturn(Mono.empty());

        //when
        final Mono<Void> result = spotManagementValidationService.validateJoinSpotRequest(spot, spotJoinRequest, customer);

        //then
        StepVerifier.create(result)
                .verifyComplete();

    }

    @Test
    void givenSpotWithInvalidPasswordAndValidJoinSpotRequestThenThrowException() {
        //given
        final Spot spot = Spot.builder()
                .requiresPassword(true)
                .password("pass123")
                .build();

        final Customer customer = Customer.builder().build();
        final SpotJoinRequest spotJoinRequest = SpotJoinRequest.builder()
                .password("pass321")
                .build();

        //when
        final Mono<Void> result = spotManagementValidationService.validateJoinSpotRequest(spot, spotJoinRequest, customer);

        //then
        StepVerifier.create(result)
                .expectError(RestIllegalRequestValueException.class)
                .verify();
    }


    @Test
    void givenSpotDuplicateWhenValidateJoinSpotRequestThenThrowException() {
        //given
        final Spot spot = Spot.builder()
                .requiresPassword(false)
                .build();

        final Customer customer = Customer.builder().build();
        final SpotJoinRequest spotJoinRequest = SpotJoinRequest.builder()
                .build();

        when(spotMembershipRepository.findBySpotAndCustomer(spot, customer))
                .thenReturn(Mono.just(SpotMembership.builder().build()));

        //when
        final Mono<Void> result = spotManagementValidationService.validateJoinSpotRequest(spot, spotJoinRequest, customer);

        //then
        StepVerifier.create(result)
                .expectError(RestIllegalRequestValueException.class)
                .verify();
    }

    @Test
    void givenInvalidMembershipStatusWhenValidateUpdateMembershipThenThrowException() {
        //given
        final SpotCompanyMembershipManagementRequest request = SpotCompanyMembershipManagementRequest.builder().membershipStatus("aa").build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPendingMembershipStatusWhenValidateUpdateMembershipThenThrowException() {
        //given
        final SpotCompanyMembershipManagementRequest request = SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.PENDING.name()).build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenBlockedMembershipStatusWhenValidateUpdateMembershipThenDoNothing() {
        //given
        final SpotCompanyMembershipManagementRequest request = SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.BLOCKED.name()).build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenActiveMembershipStatusWhenValidateUpdateMembershipThenDoNothing() {
        //given
        final SpotCompanyMembershipManagementRequest request = SpotCompanyMembershipManagementRequest.builder()
                .membershipStatus(SpotMembershipStatus.ACTIVE.name()).build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateUpdateMembership(request));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidSpotMembershipWhenValidateDeleteSpotMembershipThenDoNothing() {
        //given
        final SpotMembership membership = SpotMembership.builder().membershipStatus(SpotMembershipStatus.ACTIVE).build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateDeleteSpotMembership(membership));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenBlockedSpotMembershipWhenValidateDeleteSpotMembershipThenThrowException() {
        //given
        final SpotMembership membership = SpotMembership.builder().membershipStatus(SpotMembershipStatus.BLOCKED).build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementValidationService.validateDeleteSpotMembership(membership));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }
}