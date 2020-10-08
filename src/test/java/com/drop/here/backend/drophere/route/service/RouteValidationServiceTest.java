package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.common.exceptions.RestOperationForbiddenException;
import com.drop.here.backend.drophere.route.dto.UnpreparedRouteRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class RouteValidationServiceTest {

    @InjectMocks
    private RouteValidationService routeValidationService;

    @Test
    void givenValidRouteStatusWhenDeleteThenDelete() {
        //given
        final Route route = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateDelete(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void giveInvalidRouteStatusWhenDeleteThenDelete() {
        //given
        final Route route = Route.builder().status(null).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateDelete(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenValidRequestWhenValidateCreateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLimitedAmountWithAmountWhenValidateCreateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getProducts().get(0).setLimitedAmount(true);
        route.getProducts().get(0).setAmount(BigDecimal.valueOf(55));

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLimitedAmountWithoutAmountWhenValidateCreateThenThrowException() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getProducts().get(0).setLimitedAmount(true);
        route.getProducts().get(0).setAmount(null);

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropStartTimeWhenValidateCreateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getDrops().get(0).setStartTime("17:61");

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropEndTimeWhenValidateCreateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getDrops().get(0).setEndTime("17:61");

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropStartEndTimeChronologyWhenValidateCreateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getDrops().get(0).setStartTime("17:51");
        route.getDrops().get(0).setEndTime("17:50");

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenValidRequestWhenValidateUpdateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        final Route entity = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateUnprepared(route, entity));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidRequestInvalidStatusWhenValidateUpdateThenThrowException() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        final Route entity = Route.builder().status(null).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateUnprepared(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenLimitedAmountWithAmountWhenValidateUpdateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getProducts().get(0).setLimitedAmount(true);
        route.getProducts().get(0).setAmount(BigDecimal.valueOf(55));
        final Route entity = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateUnprepared(route, entity));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLimitedAmountWithoutAmountWhenValidateUpdateThenThrowException() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getProducts().get(0).setLimitedAmount(true);
        route.getProducts().get(0).setAmount(null);
        final Route entity = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateUnprepared(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropStartTimeWhenValidateUpdateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getDrops().get(0).setStartTime("17:61");
        final Route entity = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateUnprepared(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropEndTimeWhenValidateUpdateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getDrops().get(0).setEndTime("17:61");
        final Route entity = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateUnprepared(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropStartEndTimeChronologyWhenValidateUpdateThenDoNothing() {
        //given
        final UnpreparedRouteRequest route = RouteDataGenerator.unprepared(1);
        route.getDrops().get(0).setStartTime("17:51");
        route.getDrops().get(0).setEndTime("17:50");
        final Route entity = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateUnprepared(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenUnpreparedStatusWhenValidatePreparedUpdateThenDoNothing() {
        //given
        final Route route = Route.builder().status(RouteStatus.UNPREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validatePreparedUpdate(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidStatusWhenValidatePrepareedUpdateThenThrowException() {
        //given
        final Route route = Route.builder().status(RouteStatus.CANCELLED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validatePreparedUpdate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPreparedStatusRouteWithSellerWhenValidateOngoingUpdateThenDoNothing() {
        //given
        final Route route = Route.builder().withSeller(true).status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateOngoingUpdate(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenPreparedStatusRouteWithoutSellerWhenValidateOngoingUpdateThenThrowException() {
        //given
        final Route route = Route.builder().withSeller(false).status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateOngoingUpdate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidStatusWhenValidateOngoingUpdateThenThrowException() {
        //given
        final Route route = Route.builder().withSeller(true).status(RouteStatus.CANCELLED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateOngoingUpdate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPreparedStatusWhenValidateCancelUpdateThenDoNothing() {
        //given
        final Route route = Route.builder().status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCancelUpdate(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenOngoingStatusWhenValidateCancelUpdateThenDoNothing() {
        //given
        final Route route = Route.builder().status(RouteStatus.ONGOING).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCancelUpdate(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidStatusWhenValidateCancelUpdateThenThrowException() {
        //given
        final Route route = Route.builder().status(RouteStatus.FINISHED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCancelUpdate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenRouteWithoutProfileWhenValidateUpdateStateChangedThenDoNothing() {
        //given
        final Route route = Route.builder().withSeller(false).build();
        final AccountProfile accountProfile = AccountProfile.builder().build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateStateChanged(route, accountProfile));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenRouteWithProfileSameAsInvokerWhenValidateUpdateStateChangedThenDoNothing() {
        //given
        final Route route = Route.builder().withSeller(true).profile(AccountProfile.builder().id(5L).build()).build();
        final AccountProfile accountProfile = AccountProfile.builder().id(5L).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateStateChanged(route, accountProfile));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenRouteWithProfileDifferentThanInvokerMainProfileWhenValidateUpdateStateChangedThenDoNothing() {
        //given
        final Route route = Route.builder().withSeller(true).profile(AccountProfile.builder().id(5L).build()).build();
        final AccountProfile accountProfile = AccountProfile.builder().id(6L).profileType(AccountProfileType.MAIN).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateStateChanged(route, accountProfile));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenRouteWithProfileDifferentThanInvokerSubProfileWhenValidateUpdateStateChangedThenDoNothing() {
        //given
        final Route route = Route.builder().withSeller(true).profile(AccountProfile.builder().id(5L).build()).build();
        final AccountProfile accountProfile = AccountProfile.builder().id(6L).profileType(AccountProfileType.SUBPROFILE).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdateStateChanged(route, accountProfile));

        //then
        assertThat(throwable).isInstanceOf(RestOperationForbiddenException.class);
    }
}