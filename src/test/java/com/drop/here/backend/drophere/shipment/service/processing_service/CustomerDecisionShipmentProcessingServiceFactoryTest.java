package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentCustomerDecision;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDecisionShipmentProcessingServiceFactoryTest {

    @InjectMocks
    private CustomerDecisionShipmentProcessingServiceFactory customerDecisionShipmentProcessingServiceFactory;

    @Mock
    private CancelCustomerDecisionShipmentProcessingService cancelCustomerDecisionShipmentProcessingService;

    @Mock
    private AcceptCustomerDecisionShipmentProcessingService acceptCustomerDecisionShipmentProcessingService;

    @Test
    void givenAcceptCustomerDecisionWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentCustomerDecisionRequest shipmentCustomerDecisionRequest = ShipmentCustomerDecisionRequest.builder()
                .customerDecision(ShipmentCustomerDecision.ACCEPT)
                .build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerDecision(shipmentCustomerDecisionRequest);

        when(acceptCustomerDecisionShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);

        //when
        final ShipmentStatus status = customerDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenCancelCustomerDecisionWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentCustomerDecisionRequest shipmentCustomerDecisionRequest = ShipmentCustomerDecisionRequest.builder()
                .customerDecision(ShipmentCustomerDecision.CANCEL)
                .build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerDecision(shipmentCustomerDecisionRequest);

        when(cancelCustomerDecisionShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);

        //when
        final ShipmentStatus status = customerDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
    }

}