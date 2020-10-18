package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentProductManagementService;
import com.drop.here.backend.drophere.shipment.service.ShipmentValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancelCompanyDecisionShipmentProcessingService implements ShipmentProcessingService {
    private final ShipmentValidationService shipmentValidationService;
    private final ShipmentProductManagementService shipmentProductManagementService;
    private final ShipmentNotificationService shipmentNotificationService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        shipmentValidationService.validateCancelCompanyDecision(shipment);
        shipment.setCompanyComment(submission.getShipmentCompanyDecisionRequest().getComment());
        final ShipmentStatus newStatus = ShipmentStatus.CANCELLED;
        shipment.setCancelledAt(LocalDateTime.now());

        shipmentProductManagementService.handle(shipment, newStatus);
        shipmentNotificationService.createNotifications(shipment, newStatus, true, false);

        return newStatus;
    }
}
