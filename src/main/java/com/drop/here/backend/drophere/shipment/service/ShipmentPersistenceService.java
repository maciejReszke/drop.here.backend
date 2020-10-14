package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShipmentPersistenceService {
    private final ShipmentRepository shipmentRepository;

    public void save(Shipment shipment) {
        shipmentRepository.save(shipment);
    }

    // TODO: 13/10/2020
    public Shipment findShipment(Long shipmentId, Customer customer) {
        return null;
    }
}
