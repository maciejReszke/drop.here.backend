package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ByCustomerUpdatedShipmentProcessingService implements ShipmentProcessingService {
    private final NewShipmentProcessingService newShipmentProcessingService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        return newShipmentProcessingService.process(shipment, submission);
    }
}
