package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentProcessOperation;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.CompanyDecisionShipmentProcessingServiceFactory;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.ByCustomerUpdatedShipmentProcessingService;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.CustomerDecisionShipmentProcessingServiceFactory;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.NewShipmentProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentProcessingServiceFactoryTest {

    @InjectMocks
    private ShipmentProcessingServiceFactory shipmentProcessingServiceFactory;

    @Mock
    private NewShipmentProcessingService newShipmentProcessingService;

    @Mock
    private ByCustomerUpdatedShipmentProcessingService byCustomerUpdatedShipmentProcessingService;

    @Mock
    private CustomerDecisionShipmentProcessingServiceFactory customerDecisionShipmentProcessingServiceFactory;

    @Mock
    private CompanyDecisionShipmentProcessingServiceFactory companyDecisionShipmentProcessingServiceFactory;

    @Test
    void givenNewShipmentOperationWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerSubmission(null);
        final ShipmentProcessOperation shipmentProcessOperation = ShipmentProcessOperation.NEW;

        when(newShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);
        //when
        final ShipmentStatus result = shipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest, shipmentProcessOperation);

        //then
        assertThat(result).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenByCustomerUpdateShipmentOperationWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerSubmission(null);
        final ShipmentProcessOperation shipmentProcessOperation = ShipmentProcessOperation.BY_CUSTOMER_UPDATED;

        when(byCustomerUpdatedShipmentProcessingService.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);
        //when
        final ShipmentStatus result = shipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest, shipmentProcessOperation);

        //then
        assertThat(result).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenCustomerDecisionShipmentOperationWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerSubmission(null);
        final ShipmentProcessOperation shipmentProcessOperation = ShipmentProcessOperation.CUSTOMER_DECISION;

        when(customerDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);
        //when
        final ShipmentStatus result = shipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest, shipmentProcessOperation);

        //then
        assertThat(result).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenCompanyDecisionShipmentOperationWhenProcessThenProcess() {
        //given
        final Shipment shipment = Shipment.builder().build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerSubmission(null);
        final ShipmentProcessOperation shipmentProcessOperation = ShipmentProcessOperation.COMPANY_DECISION;

        when(companyDecisionShipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest)).thenReturn(ShipmentStatus.PLACED);
        //when
        final ShipmentStatus result = shipmentProcessingServiceFactory.process(shipment, shipmentProcessingRequest, shipmentProcessOperation);

        //then
        assertThat(result).isEqualTo(ShipmentStatus.PLACED);
    }

}