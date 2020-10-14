package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ShipmentSearchingService {

    // TODO: 13/10/2020
    public Page<ShipmentCustomerResponse> findCustomerShipments(Customer customer, String status, Pageable pageable) {
        return null;
    }
}
