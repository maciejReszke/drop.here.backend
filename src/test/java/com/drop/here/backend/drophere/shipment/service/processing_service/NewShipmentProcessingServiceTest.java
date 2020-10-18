package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.service.RouteService;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentProductManagementService;
import com.drop.here.backend.drophere.test_data.ShipmentDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewShipmentProcessingServiceTest {

    @InjectMocks
    private NewShipmentProcessingService processingService;

    @Mock
    private RouteService routeService;

    @Mock
    private ShipmentNotificationService shipmentNotificationService;

    @Mock
    private ShipmentProductManagementService shipmentProductManagementService;

    @Test
    void givenShipmentNewStatusIsPlacedWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerSubmission(
                ShipmentDataGenerator.customerSubmissionRequest(1));

        when(routeService.getSubmittedShipmentStatus(drop)).thenReturn(ShipmentStatus.PLACED);
        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.PLACED, false, true);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.PLACED);
        verifyNoMoreInteractions(shipmentProductManagementService);
    }

    @Test
    void givenShipmentNewStatusIsAcceptedWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.customerSubmission(
                ShipmentDataGenerator.customerSubmissionRequest(1));

        when(routeService.getSubmittedShipmentStatus(drop)).thenReturn(ShipmentStatus.ACCEPTED);
        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.ACCEPTED, false, true);
        doNothing().when(shipmentProductManagementService).subtract(shipment);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.ACCEPTED);
    }

}