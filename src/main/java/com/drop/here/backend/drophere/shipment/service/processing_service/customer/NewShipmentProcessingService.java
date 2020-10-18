package com.drop.here.backend.drophere.shipment.service.processing_service.customer;

import com.drop.here.backend.drophere.route.service.RouteService;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.ShipmentNotificationService;
import com.drop.here.backend.drophere.shipment.service.ShipmentProductManagementService;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingService;
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
            shipment.setAcceptedAt(LocalDateTime.now());
            shipmentProductManagementService.reduce(shipment);
        }

        shipmentNotificationService.createNotifications(shipment, newStatus, false, true);
        return newStatus;
    }

}
