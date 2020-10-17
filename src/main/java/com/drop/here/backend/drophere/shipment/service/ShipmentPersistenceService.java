package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
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

    public Shipment findShipment(Long shipmentId, Customer customer) {
        return shipmentRepository.findByIdAndCustomer(shipmentId, customer)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Shipment with id %s for customer %s was not found", shipmentId, customer.getId()),
                        RestExceptionStatusCode.SHIPMENT_BY_ID_DOES_NOT_EXIST
                ));
    }
}
