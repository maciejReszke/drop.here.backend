package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
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
        final Route route = Route.builder().status(RouteStatus.PREPARED).build();

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
        final RouteRequest route = RouteDataGenerator.request(1);

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLimitedAmountWithAmountWhenValidateCreateThenDoNothing() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
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
        final RouteRequest route = RouteDataGenerator.request(1);
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
        final RouteRequest route = RouteDataGenerator.request(1);
        route.getDrops().get(0).setStartTime("17:61");

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropEndTimeWhenValidateCreateThenDoNothing() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
        route.getDrops().get(0).setEndTime("17:61");

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateCreate(route));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropStartEndTimeChronologyWhenValidateCreateThenDoNothing() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
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
        final RouteRequest route = RouteDataGenerator.request(1);
        final Route entity = Route.builder().status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdate(route, entity));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidRequestInvalidStatusWhenValidateUpdateThenThrowException() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
        final Route entity = Route.builder().status(null).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdate(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenLimitedAmountWithAmountWhenValidateUpdateThenDoNothing() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
        route.getProducts().get(0).setLimitedAmount(true);
        route.getProducts().get(0).setAmount(BigDecimal.valueOf(55));
        final Route entity = Route.builder().status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdate(route, entity));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLimitedAmountWithoutAmountWhenValidateUpdateThenThrowException() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
        route.getProducts().get(0).setLimitedAmount(true);
        route.getProducts().get(0).setAmount(null);
        final Route entity = Route.builder().status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdate(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropStartTimeWhenValidateUpdateThenDoNothing() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
        route.getDrops().get(0).setStartTime("17:61");
        final Route entity = Route.builder().status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdate(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropEndTimeWhenValidateUpdateThenDoNothing() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
        route.getDrops().get(0).setEndTime("17:61");
        final Route entity = Route.builder().status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdate(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidDropStartEndTimeChronologyWhenValidateUpdateThenDoNothing() {
        //given
        final RouteRequest route = RouteDataGenerator.request(1);
        route.getDrops().get(0).setStartTime("17:51");
        route.getDrops().get(0).setEndTime("17:50");
        final Route entity = Route.builder().status(RouteStatus.PREPARED).build();

        //when
        final Throwable throwable = catchThrowable(() -> routeValidationService.validateUpdate(route, entity));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }
}