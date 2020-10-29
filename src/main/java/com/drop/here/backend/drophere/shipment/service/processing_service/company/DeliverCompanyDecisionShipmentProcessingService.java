package com.drop.here.backend.drophere.shipment.service.processing_service.company;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentValidationService;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliverCompanyDecisionShipmentProcessingService implements ShipmentProcessingService {
    private final ShipmentValidationService shipmentValidationService;
    private final ShipmentNotificationService shipmentNotificationService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        shipmentValidationService.validateDeliverCompanyDecision(shipment);
        shipment.setCompanyComment(submission.getShipmentCompanyDecisionRequest().getComment());
        final ShipmentStatus newStatus = ShipmentStatus.DELIVERED;

        shipment.setDeliveredAt(LocalDateTime.now());

        shipmentNotificationService.createNotifications(shipment, newStatus, true, false);

        return newStatus;
    }
}
