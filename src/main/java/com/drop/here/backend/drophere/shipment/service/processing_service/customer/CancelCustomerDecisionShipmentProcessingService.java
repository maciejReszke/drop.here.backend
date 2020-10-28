package com.drop.here.backend.drophere.shipment.service.processing_service.customer;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
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
    private final ShipmentNotificationService shipmentNotificationService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        shipmentValidationService.validateCancelCustomerDecision(shipment);
        shipment.setCustomerComment(submission.getShipmentCustomerDecisionRequest().getComment());
        final ShipmentStatus newStatus = handleByStatus(shipment);
        shipmentNotificationService.createNotifications(shipment, newStatus, false, true);
        return newStatus;
    }

    private ShipmentStatus handleByStatus(Shipment shipment) {
        return API.Match(shipment.getStatus()).of(
                Case($(ShipmentStatus.ACCEPTED), () -> handleAcceptedShipment(shipment)),
                Case($(ShipmentStatus.PLACED), () -> handlePlacedShipment(shipment))
        );
    }

    private ShipmentStatus handlePlacedShipment(Shipment shipment) {
        shipment.setCancelledAt(LocalDateTime.now());
        return ShipmentStatus.CANCELLED;
    }

    private ShipmentStatus handleAcceptedShipment(Shipment shipment) {
        shipment.setCancelRequestedAt(LocalDateTime.now());
        return ShipmentStatus.CANCEL_REQUESTED;
    }
}
