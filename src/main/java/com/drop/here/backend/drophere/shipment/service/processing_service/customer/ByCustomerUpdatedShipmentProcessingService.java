package com.drop.here.backend.drophere.shipment.service.processing_service.customer;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ByCustomerUpdatedShipmentProcessingService implements ShipmentProcessingService {
    private final NewShipmentProcessingService newShipmentProcessingService;

    // TODO: 18/10/2020 fix! (jezeli byl compromised to mozna tez przyjac, i wtedy trzeba zrobic add i do placed powinno isc, ale zanim zmapuje nowe rzeczy!)
    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        return newShipmentProcessingService.process(shipment, submission);
    }
}
