package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentFlow;
import com.drop.here.backend.drophere.shipment.repository.ShipmentFlowRepository;
import com.drop.here.backend.drophere.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ShipmentPersistenceService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentFlowRepository shipmentFlowRepository;

    @Transactional(rollbackFor = Exception.class)
    public void save(Shipment shipment) {
        shipmentRepository.save(shipment);
        saveFlow(shipment);
    }

    private void saveFlow(Shipment shipment) {
        shipmentFlowRepository.save(ShipmentFlow.builder()
                .createdAt(LocalDateTime.now())
                .shipment(shipment)
                .status(shipment.getStatus())
                .build());
    }

    public Shipment findShipment(Long shipmentId, Customer customer) {
        return shipmentRepository.findByIdAndCustomer(shipmentId, customer)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Shipment with id %s for customer %s was not found", shipmentId, customer.getId()),
                        RestExceptionStatusCode.SHIPMENT_BY_ID_AND_CUSTOMER_DOES_NOT_EXIST
                ));
    }

    public Shipment findShipment(Long shipmentId, Company company) {
        return shipmentRepository.findByIdAndCompany(shipmentId, company)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Shipment with id %s for company %s was not found", shipmentId, company.getUid()),
                        RestExceptionStatusCode.SHIPMENT_BY_ID_AND_COMPANY_DOES_NOT_EXIST
                ));
    }

    public List<ShipmentFlow> findShipmentsFlows(List<Long> shipmentsIds) {
        return shipmentFlowRepository.findAllByShipmentIdIn(shipmentsIds);
    }
}
