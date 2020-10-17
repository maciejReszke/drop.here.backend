package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomizationRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProductRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ShipmentDataGenerator {

    public ShipmentCustomerSubmissionRequest customerSubmissionRequest(int id) {
        return ShipmentCustomerSubmissionRequest.builder()
                .comment("customerComment" + id)
                .products(List.of(shipmentProductRequest(2 * id), shipmentProductRequest(2 * id + 1)))
                .build();
    }

    private ShipmentProductRequest shipmentProductRequest(int i) {
        return ShipmentProductRequest.builder()
                .customizations(List.of(shipmentCustomizationRequest(i)))
                .quantity(BigDecimal.ONE)
                .routeProductId(5L + i)
                .build();
    }

    private ShipmentCustomizationRequest shipmentCustomizationRequest(int id) {
        return ShipmentCustomizationRequest.builder()
                .id(id + 3L)
                .build();
    }

    public Shipment shipment(int i, Drop drop, Company company, Customer customer, Set<ShipmentProduct> products) {
        return Shipment.builder()
                .company(company)
                .drop(drop)
                .customerComment("customerComment" + i)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .status(ShipmentStatus.PLACED)
                .companyComment("companyComment" + i)
                .summarizedAmount(BigDecimal.valueOf(55.33))
                .products(products)
                .build();
    }
}
