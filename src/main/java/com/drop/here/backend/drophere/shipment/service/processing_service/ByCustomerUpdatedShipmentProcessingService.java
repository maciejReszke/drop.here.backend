package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ByCustomerUpdatedShipmentProcessingService implements ShipmentProcessingService {

    // TODO: 13/10/2020 dwie opcje - palced lub compromised
    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        return null;
    }
}
