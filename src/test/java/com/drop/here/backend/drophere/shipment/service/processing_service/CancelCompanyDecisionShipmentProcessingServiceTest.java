package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCompanyDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentProductManagementService;
import com.drop.here.backend.drophere.shipment.service.ShipmentValidationService;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.CancelCompanyDecisionShipmentProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class CancelCompanyDecisionShipmentProcessingServiceTest {
    @InjectMocks
    private CancelCompanyDecisionShipmentProcessingService processingService;

    @Mock
    private ShipmentValidationService shipmentValidationService;

    @Mock
    private ShipmentNotificationService shipmentNotificationService;

    @Mock
    private ShipmentProductManagementService shipmentProductManagementService;

    @Test
    void givenShipmentAcceptedWhenProcessThenProcess() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).drop(drop).build();
        final ShipmentProcessingRequest shipmentProcessingRequest = ShipmentProcessingRequest.companyDecision(
                ShipmentCompanyDecisionRequest.builder().comment("companyComment123").build()
        );

        doNothing().when(shipmentNotificationService).createNotifications(shipment, ShipmentStatus.CANCELLED, true, false);
        doNothing().when(shipmentProductManagementService).increase(shipment);
        doNothing().when(shipmentValidationService).validateCancelCompanyDecision(shipment);

        //when
        final ShipmentStatus status = processingService.process(shipment, shipmentProcessingRequest);

        //then
        assertThat(status).isEqualTo(ShipmentStatus.CANCELLED);
        assertThat(shipment.getCancelledAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(shipment.getCompanyComment()).isEqualTo("companyComment123");
    }
}