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
public class AcceptCustomerDecisionShipmentProcessingService implements ShipmentProcessingService {
    private final ShipmentValidationService shipmentValidationService;
    private final ShipmentProductManagementService shipmentProductManagementService;
    private final ShipmentNotificationService shipmentNotificationService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        shipmentValidationService.validateAcceptCustomerDecision(shipment);
        shipment.setCustomerComment(submission.getShipmentCustomerDecisionRequest().getComment());
        final ShipmentStatus newStatus = ShipmentStatus.ACCEPTED;

        shipment.setAcceptedAt(LocalDateTime.now());

        shipmentProductManagementService.handle(shipment, newStatus);
        shipmentNotificationService.createNotifications(shipment, newStatus, false, true);

        return newStatus;
    }
}
