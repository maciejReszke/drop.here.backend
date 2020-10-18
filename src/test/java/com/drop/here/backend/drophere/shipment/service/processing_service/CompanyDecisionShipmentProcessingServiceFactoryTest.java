package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentCompanyDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentCompanyDecision;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.AcceptCompanyDecisionShipmentProcessingService;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.CancelCompanyDecisionShipmentProcessingService;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.CompanyDecisionShipmentProcessingServiceFactory;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.DeliverCompanyDecisionShipmentProcessingService;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.RejectCompanyDecisionShipmentProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyDecisionShipmentProcessingServiceFactoryTest {
    @InjectMocks
    private CompanyDecisionShipmentProcessingServiceFactory companyDecisionShipmentProcessingServiceFactory;

    @Mock
    private AcceptCompanyDecisionShipmentProcessingService acceptCompanyDecisionShipmentProcessingService;

    @Mock
    private RejectCompanyDecisionShipmentProcessingService rejectCompanyDecisionShipmentProcessingService;

    @Mock
    private CancelCompanyDecisionShipmentProcessingService cancelCompanyDecisionShipmentProcessingService;

    @Mock
    private DeliverCompanyDecisionShipmentProcessingService deliverCompanyDecisionShipmentProcessingService;

    @Test
    void givenAcceptCompanyDecisionWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentCompanyDecisionRequest shipmentCompanyDecisionRequest = ShipmentCompanyDecisionRequest.builder()
                .companyDecision(ShipmentCompanyDecision.ACCEPT)
                .build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(shipmentCompanyDecisionRequest);

        when(acceptCompanyDecisionShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);

        //when
        final ShipmentStatus status = companyDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenRejectCompanyDecisionWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentCompanyDecisionRequest shipmentCompanyDecisionRequest = ShipmentCompanyDecisionRequest.builder()
                .companyDecision(ShipmentCompanyDecision.REJECT)
                .build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(shipmentCompanyDecisionRequest);

        when(rejectCompanyDecisionShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);

        //when
        final ShipmentStatus status = companyDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenCancelCompanyDecisionWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentCompanyDecisionRequest shipmentCompanyDecisionRequest = ShipmentCompanyDecisionRequest.builder()
                .companyDecision(ShipmentCompanyDecision.CANCEL)
                .build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(shipmentCompanyDecisionRequest);

        when(cancelCompanyDecisionShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);

        //when
        final ShipmentStatus status = companyDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenDeliverCompanyDecisionWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentCompanyDecisionRequest shipmentCompanyDecisionRequest = ShipmentCompanyDecisionRequest.builder()
                .companyDecision(ShipmentCompanyDecision.DELIVER)
                .build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(shipmentCompanyDecisionRequest);

        when(deliverCompanyDecisionShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);

        //when
        final ShipmentStatus status = companyDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
    }

}