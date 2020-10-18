package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.route.service.RouteService;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentProductManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NewShipmentProcessingService implements ShipmentProcessingService {
    private final RouteService routeService;
    private final ShipmentNotificationService shipmentNotificationService;
    private final ShipmentProductManagementService shipmentProductManagementService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        final ShipmentStatus newStatus = routeService.getSubmittedShipmentStatus(shipment.getDrop());
        shipment.setPlacedAt(LocalDateTime.now());
        if (newStatus == ShipmentStatus.ACCEPTED) {
            handleAcceptedShipment(shipment);
        }
        shipmentNotificationService.createNotifications(shipment, newStatus, false, true);
        return newStatus;
    }

    private void handleAcceptedShipment(Shipment shipment) {
        shipment.setAcceptedAt(shipment.getStatus() == ShipmentStatus.ACCEPTED ? LocalDateTime.now() : null);
        shipmentProductManagementService.subtract(shipment);
    }
}
