package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class ShipmentValidationServiceTest {

    @InjectMocks
    private ShipmentValidationService shipmentValidationService;

    @Test
    void givenCompromisedShipmentWhenValidateRejectCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenPlacedShipmentWhenValidateRejectCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateRejectCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateRejectCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenAcceptedShipmentWhenValidateDeliverCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateDeliverCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenCancelRequestedShipmentWhenValidateDeliverCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.CANCEL_REQUESTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateDeliverCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateDeliverCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateDeliverCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPlacedShipmentWhenValidateAcceptCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateAcceptCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateAcceptCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenCancelRequestedShipmentWhenValidateCancelCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.CANCEL_REQUESTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateCancelCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenCompromisedShipmentWhenValidateAcceptCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateAcceptCustomerDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCustomerDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenAcceptedShipmentWhenValidateCancelCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenPlacedShipmentWhenValidateCancelCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenCompromisedShipmentWhenValidateCancelCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateCancelCustomerDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPlacedShipmentWhenValidateShipmentCustomerUpdateThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipmentCustomerUpdate(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateCustomerShipmentUpdateThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipmentCustomerUpdate(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }
}