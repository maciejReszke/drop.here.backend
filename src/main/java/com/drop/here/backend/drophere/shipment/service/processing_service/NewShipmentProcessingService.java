package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import org.springframework.stereotype.Service;

@Service
public class NewShipmentProcessingService implements ShipmentProcessingService {

    // TODO: 13/10/2020
    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        return null;
    }
}
