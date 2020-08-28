package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class DropManagementValidationServiceTest {

    @InjectMocks
    private DropManagementValidationService dropManagementValidationService;

    @Test
    void givenValidGeolocationWithPasswordRequestWhenValidateDropRequestThenDoNothing() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .locationDropType(DropLocationType.GEOLOCATION.name())
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
    void givenValidGeolocationWithoutPasswordRequestWhenValidateDropRequestThenDoNothing() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .locationDropType(DropLocationType.GEOLOCATION.name())
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
    void givenHiddenLocationWithoutPasswordRequestWhenValidateDropRequestThenDoNothing() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .locationDropType(DropLocationType.HIDDEN.name())
                .xCoordinate(null)
                .yCoordinate(null)
                .estimatedRadiusMeters(null)
                .password(null)
                .requiresPassword(false)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDropRequest(dropManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidGeolocationLackOfXCoordinationWhenValidateDropRequestThenError() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .locationDropType(DropLocationType.GEOLOCATION.name())
                .xCoordinate(null)
                .yCoordinate(1D)
                .estimatedRadiusMeters(100)
                .password("a")
                .requiresPassword(true)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDropRequest(dropManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class)
                .matches(t -> ((RestIllegalRequestValueException) (t)).getCode() ==
                        RestExceptionStatusCode.DROP_GEOLOCATION_NULL_LOCATION_PROPERTY.ordinal());
    }

    @Test
    void givenInvalidGeolocationLackOfYCoordinationWhenValidateDropRequestThenError() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .locationDropType(DropLocationType.GEOLOCATION.name())
                .xCoordinate(1D)
                .yCoordinate(null)
                .estimatedRadiusMeters(100)
                .password("a")
                .requiresPassword(true)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDropRequest(dropManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class)
                .matches(t -> ((RestIllegalRequestValueException) (t)).getCode() ==
                        RestExceptionStatusCode.DROP_GEOLOCATION_NULL_LOCATION_PROPERTY.ordinal());
    }

    @Test
    void givenInvalidGeolocationLackOfRadiusWhenValidateDropRequestThenError() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .locationDropType(DropLocationType.GEOLOCATION.name())
                .xCoordinate(1D)
                .yCoordinate(1D)
                .estimatedRadiusMeters(null)
                .password("a")
                .requiresPassword(true)
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementValidationService.validateDropRequest(dropManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class)
                .matches(t -> ((RestIllegalRequestValueException) (t)).getCode() ==
                        RestExceptionStatusCode.DROP_GEOLOCATION_NULL_LOCATION_PROPERTY.ordinal());
    }

    @Test
    void givenLackOfPasswordWhenWithPasswordWhenValidateDropRequestThenError() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder()
                .locationDropType(DropLocationType.HIDDEN.name())
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
}