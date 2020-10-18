package com.drop.here.backend.drophere.shipment.service.processing_service.company;

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
public class RejectCompanyDecisionShipmentProcessingService implements ShipmentProcessingService {
    private final ShipmentValidationService shipmentValidationService;
    private final ShipmentProductManagementService shipmentProductManagementService;
    private final ShipmentNotificationService shipmentNotificationService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        shipmentValidationService.validateRejectCompanyDecision(shipment);
        shipment.setCompanyComment(submission.getShipmentCompanyDecisionRequest().getComment());
        final ShipmentStatus newStatus = ShipmentStatus.REJECTED;

        shipment.setRejectedAt(LocalDateTime.now());

        manageProductsQuantityChange(shipment);

        shipmentNotificationService.createNotifications(shipment, newStatus, true, false);

        return newStatus;
    }

    private void manageProductsQuantityChange(Shipment shipment) {
        API.Match(shipment.getStatus()).of(
                Case($(ShipmentStatus.COMPROMISED), () -> API.run(() -> shipmentProductManagementService.increase(shipment))),
                Case($(ShipmentStatus.ACCEPTED), () -> API.run(() -> shipmentProductManagementService.increase(shipment))),
                Case($(ShipmentStatus.PLACED), () -> API.run(this::doNothing))
        );
    }

    private void doNothing() {
        //does nothing
    }
}
