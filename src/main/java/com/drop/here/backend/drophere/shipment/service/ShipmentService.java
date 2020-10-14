package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerResponse;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentProcessOperation;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ShipmentService {
    private final ShipmentValidationService shipmentValidationService;
    private final ShipmentMappingService shipmentMappingService;
    private final DropService dropService;
    private final ShipmentProcessingServiceFactory shipmentProcessingServiceFactory;
    private final ShipmentPersistenceService shipmentPersistenceService;
    private final ShipmentSearchingService shipmentSearchingService;

    // TODO: 12/10/2020  test
    public ResourceOperationResponse createShipment(String dropUid, ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest, AccountAuthentication authentication) {
        final Customer customer = authentication.getCustomer();
        final Drop drop = dropService.findPrivilegedDrop(dropUid, customer);
        shipmentValidationService.validateCreateShipmentRequest(shipmentCustomerSubmissionRequest);
        final Shipment shipment = shipmentMappingService.toEntity(drop, shipmentCustomerSubmissionRequest, customer);
        final ShipmentStatus shipmentStatus = shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerSubmission(shipmentCustomerSubmissionRequest), ShipmentProcessOperation.NEW);
        log.info("Created new shipment with status {} for customer {} drop {}", shipmentStatus, customer.getId(), drop.getUid());
        shipment.setShipmentStatus(shipmentStatus);
        shipmentPersistenceService.save(shipment);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, shipment.getId());
    }

    public Page<ShipmentCustomerResponse> findCustomerShipments(AccountAuthentication authentication, String status, Pageable pageable) {
        return shipmentSearchingService.findCustomerShipments(authentication.getCustomer(), status, pageable);
    }

    // TODO: 13/10/2020  test
    public ResourceOperationResponse updateShipment(Long shipmentId, ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest, AccountAuthentication authentication) {
        final Shipment shipment = shipmentPersistenceService.findShipment(shipmentId, authentication.getCustomer());
        shipmentValidationService.validateUpdateShipmentRequest(shipment, shipmentCustomerSubmissionRequest);
        shipmentMappingService.update(shipment, shipmentCustomerSubmissionRequest);
        final ShipmentStatus shipmentStatus = shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerSubmission(shipmentCustomerSubmissionRequest), ShipmentProcessOperation.BY_CUSTOMER_UPDATED);
        return updateShipment(authentication, shipment, shipmentStatus);
    }

    // TODO: 13/10/2020 test
    public ResourceOperationResponse updateShipmentStatus(Long shipmentId, ShipmentCustomerDecisionRequest shipmentCustomerDecisionRequest, AccountAuthentication authentication) {
        final Shipment shipment = shipmentPersistenceService.findShipment(shipmentId, authentication.getCustomer());
        final ShipmentStatus shipmentStatus = shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerDecision(shipmentCustomerDecisionRequest), ShipmentProcessOperation.CUSTOMER_DECISION);
        return updateShipment(authentication, shipment, shipmentStatus);
    }

    private ResourceOperationResponse updateShipment(AccountAuthentication authentication, Shipment shipment, ShipmentStatus shipmentStatus) {
        log.info("Updated shipment with status {} to {} for customer {} shipment {}", shipment.getShipmentStatus(), shipmentStatus, authentication.getCustomer().getId(), shipment.getId());
        shipment.setShipmentStatus(shipmentStatus);
        shipmentPersistenceService.save(shipment);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, shipment.getId());
    }
}
