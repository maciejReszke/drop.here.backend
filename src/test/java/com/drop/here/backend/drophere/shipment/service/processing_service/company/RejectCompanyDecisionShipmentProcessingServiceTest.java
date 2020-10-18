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
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RejectCompanyDecisionShipmentProcessingServiceTest {
    @InjectMocks
    private RejectCompanyDecisionShipmentProcessingService processingService;

    @Mock
    private ShipmentValidationService shipmentValidationService;

    @Mock
    private ShipmentNotificationService shipmentNotificationService;

    @Mock
    private ShipmentProductManagementService shipmentProductManagementService;

    @Test
    void givenCompromisedShipmentWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED)
                .acceptedAt(LocalDateTime.now()).drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(
                ShipmentCompanyDecisionRequest.builder().comment("companyComment123").build()
        );

        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.REJECTED, true, false);
        doNothing().when(shipmentProductManagementService).increase(shipment);
        doNothing().when(shipmentValidationService).validateRejectCompanyDecision(shipment);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.REJECTED);
        assertThat(shipment.getRejectedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(shipment.getCompanyComment()).isEqualTo("companyComment123");
    }

    @Test
    void givenPlacedShipmentWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED)
                .acceptedAt(LocalDateTime.now()).drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(
                ShipmentCompanyDecisionRequest.builder().comment("companyComment123").build()
        );

        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.REJECTED, true, false);
        doNothing().when(shipmentValidationService).validateRejectCompanyDecision(shipment);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.REJECTED);
        assertThat(shipment.getRejectedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(shipment.getCompanyComment()).isEqualTo("companyComment123");
        verifyNoMoreInteractions(shipmentProductManagementService);
    }

    @Test
    void givenAcceptedShipmentWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED)
                .acceptedAt(LocalDateTime.now()).drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(
                ShipmentCompanyDecisionRequest.builder().comment("companyComment123").build()
        );

        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.REJECTED, true, false);
        doNothing().when(shipmentProductManagementService).increase(shipment);
        doNothing().when(shipmentValidationService).validateRejectCompanyDecision(shipment);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.REJECTED);
        assertThat(shipment.getRejectedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(shipment.getCompanyComment()).isEqualTo("companyComment123");
    }
}