package com.drop.here.backend.drophere.shipment.service.processing_service.customer;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentProductManagementService;
import com.drop.here.backend.drophere.shipment.service.ShipmentValidationService;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingService;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class CancelCustomerDecisionShipmentProcessingService implements ShipmentProcessingService {
    private final ShipmentValidationService shipmentValidationService;
    private final ShipmentProductManagementService shipmentProductManagementService;
    private final ShipmentNotificationService shipmentNotificationService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        shipmentValidationService.validateCancelCustomerDecision(shipment);
        shipment.setCustomerComment(submission.getShipmentCustomerDecisionRequest().getComment());
        final ShipmentStatus newStatus = getShipmentStatus(shipment);

        if (newStatus == ShipmentStatus.CANCEL_REQUESTED) {
            shipment.setCancelRequestedAt(LocalDateTime.now());
        }

        if (newStatus == ShipmentStatus.CANCELLED) {
            shipment.setCancelledAt(LocalDateTime.now());
        }

        shipmentProductManagementService.handle(shipment, newStatus);
        shipmentNotificationService.createNotifications(shipment, newStatus, false, true);

        return newStatus;
    }

    private ShipmentStatus getShipmentStatus(Shipment shipment) {
        return API.Match(shipment.getStatus()).of(
                Case($(ShipmentStatus.ACCEPTED), ShipmentStatus.CANCEL_REQUESTED),
                Case($(ShipmentStatus.PLACED), ShipmentStatus.CANCELLED),
                Case($(ShipmentStatus.COMPROMISED), ShipmentStatus.CANCELLED)
        );
    }
}
