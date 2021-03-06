package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class ShipmentMappingService {
    private final ShipmentCalculatingService shipmentCalculatingService;
    private final ShipmentProductMappingService shipmentProductMappingService;

    public Shipment toEntity(Drop drop, ShipmentCustomerSubmissionRequest request, Customer customer) {
        final Shipment shipment = Shipment.builder()
                .customer(customer)
                .drop(drop)
                .company(drop.getSpot().getCompany())
                .createdAt(LocalDateTime.now())
                .products(new LinkedHashSet<>())
                .build();

        update(shipment, request);

        return shipment;
    }

    public void update(Shipment shipment, ShipmentCustomerSubmissionRequest request) {
        shipment.getProducts().forEach(product -> product.setShipment(null));
        shipment.getProducts().clear();
        shipment.setUpdatedAt(LocalDateTime.now());
        shipment.setCustomerComment(request.getComment());
        shipment.getProducts().addAll(shipmentProductMappingService.createShipmentProducts(shipment, request));
        shipment.setSummarizedAmount(shipmentCalculatingService.calculateShipment(shipment));
    }
}
