package com.drop.here.backend.drophere.shipment.service.processing_service.company;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingService;
import org.springframework.stereotype.Service;

@Service
public class RejectCompanyDecisionShipmentProcessingService implements ShipmentProcessingService {

    // TODO: 13/10/2020 test, implement
    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest submission) {
        return null;
    }
}
