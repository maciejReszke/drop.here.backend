package com.drop.here.backend.drophere.shipment.service.processing_service.company;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCompanyDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentProductManagementService;
import com.drop.here.backend.drophere.shipment.service.ShipmentValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AcceptCompanyDecisionShipmentProcessingServiceTest {
    @InjectMocks
    private AcceptCompanyDecisionShipmentProcessingService processingService;

    @Mock
    private ShipmentValidationService shipmentValidationService;

    @Mock
    private ShipmentNotificationService shipmentNotificationService;

    @Mock
    private ShipmentProductManagementService shipmentProductManagementService;

    @Test
    void givenDeliveredShipmentWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED)
                .deliveredAt(LocalDateTime.now()).drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(
                ShipmentCompanyDecisionRequest.builder().comment("companyComment123").build()
        );

        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.ACCEPTED, true, false);
        doNothing().when(shipmentProductManagementService).handle(shipment, ShipmentStatus.ACCEPTED);
        doNothing().when(shipmentValidationService).validateAcceptCompanyDecision(shipment);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.ACCEPTED);
        assertThat(shipment.getAcceptedAt()).isNull();
        assertThat(shipment.getDeliveredAt()).isNull();
        assertThat(shipment.getCompanyComment()).isEqualTo("companyComment123");
    }

    @Test
    void givenNotDeliveredShipmentWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED)
                .deliveredAt(LocalDateTime.now()).drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(
                ShipmentCompanyDecisionRequest.builder().comment("companyComment123").build()
        );

        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.ACCEPTED, true, false);
        doNothing().when(shipmentProductManagementService).handle(shipment, ShipmentStatus.ACCEPTED);
        doNothing().when(shipmentValidationService).validateAcceptCompanyDecision(shipment);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.ACCEPTED);
        assertThat(shipment.getAcceptedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(shipment.getDeliveredAt()).isNull();
        assertThat(shipment.getCompanyComment()).isEqualTo("companyComment123");
    }
}