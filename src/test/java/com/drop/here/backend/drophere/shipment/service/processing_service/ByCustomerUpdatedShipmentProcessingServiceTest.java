package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.ByCustomerUpdatedShipmentProcessingService;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.NewShipmentProcessingService;
import com.drop.here.backend.drophere.test_data.ShipmentDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ByCustomerUpdatedShipmentProcessingServiceTest {
    @InjectMocks
    private ByCustomerUpdatedShipmentProcessingService processingService;

    @Mock
    private NewShipmentProcessingService newShipmentProcessingService;

    @Test
    void givenShipmentWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerSubmission(
                ShipmentDataGenerator.customerSubmissionRequest(1));

        when(newShipmentProcessingService.process(shipment, shipmentProcessingRequest))
                .thenReturn(ShipmentStatus.PLACED);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
    }
}