package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import org.springframework.stereotype.Service;

@Service
public class ShipmentMappingService {

    // TODO: 13/10/2020 test, implement
    public Shipment toEntity(Drop drop, ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest, Customer customer) {

        return null;
    }

    // TODO: 13/10/2020  test, impppemetn

    public void update(Shipment shipment, ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest) {

    }
}
