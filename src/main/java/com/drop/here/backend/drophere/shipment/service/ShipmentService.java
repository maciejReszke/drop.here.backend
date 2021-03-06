package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCompanyDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentResponse;
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

import java.time.LocalDateTime;

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

    public ResourceOperationResponse createShipment(String dropUid, ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest, AccountAuthentication authentication) {
        final Customer customer = authentication.getCustomer();
        final Drop drop = dropService.findPrivilegedDrop(dropUid, customer, true);
        final Shipment shipment = shipmentMappingService.toEntity(drop, shipmentCustomerSubmissionRequest, customer);
        shipmentValidationService.validateShipment(shipment);
        final ShipmentStatus shipmentStatus = shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerSubmission(shipmentCustomerSubmissionRequest), ShipmentProcessOperation.NEW);
        log.info("Created new shipment with status {} for customer {} drop {}", shipmentStatus, customer.getId(), drop.getUid());
        shipment.setStatus(shipmentStatus);
        shipmentPersistenceService.save(shipment);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, shipment.getId());
    }

    public ShipmentResponse findCustomerShipment(AccountAuthentication authentication, Long shipmentId) {
        return shipmentSearchingService.findCustomerShipment(authentication.getCustomer(), shipmentId);
    }

    public Page<ShipmentResponse> findCustomerShipments(AccountAuthentication authentication, String status, Pageable pageable) {
        return shipmentSearchingService.findCustomerShipments(authentication.getCustomer(), status, pageable);
    }

    public ResourceOperationResponse update(Long shipmentId, ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest, AccountAuthentication authentication) {
        final Shipment shipment = shipmentPersistenceService.findShipment(shipmentId, authentication.getCustomer());
        shipmentValidationService.validateShipmentCustomerUpdate(shipment);
        shipmentMappingService.update(shipment, shipmentCustomerSubmissionRequest);
        shipmentValidationService.validateShipment(shipment);
        final ShipmentStatus shipmentStatus = shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerSubmission(shipmentCustomerSubmissionRequest), ShipmentProcessOperation.BY_CUSTOMER_UPDATED);
        return update(shipment, shipmentStatus);
    }

    public ResourceOperationResponse updateShipmentStatus(Long shipmentId, ShipmentCustomerDecisionRequest shipmentCustomerDecisionRequest, AccountAuthentication authentication) {
        final Shipment shipment = shipmentPersistenceService.findShipment(shipmentId, authentication.getCustomer());
        final ShipmentStatus shipmentStatus = shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerDecision(shipmentCustomerDecisionRequest), ShipmentProcessOperation.CUSTOMER_DECISION);
        return update(shipment, shipmentStatus);
    }

    public ResourceOperationResponse updateShipmentStatus(Long shipmentId, ShipmentCompanyDecisionRequest shipmentCustomerDecisionRequest, AccountAuthentication authentication) {
        final Shipment shipment = shipmentPersistenceService.findShipment(shipmentId, authentication.getCompany());
        final ShipmentStatus shipmentStatus = shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.companyDecision(shipmentCustomerDecisionRequest), ShipmentProcessOperation.COMPANY_DECISION);
        return update(shipment, shipmentStatus);
    }

    private ResourceOperationResponse update(Shipment shipment, ShipmentStatus shipmentStatus) {
        log.info("Updated shipment with status {} to {} for customer {} company {} shipment {}", shipment.getStatus(), shipmentStatus, shipment.getCustomer().getId(), shipment.getCompany().getId(), shipment.getId());
        shipment.setStatus(shipmentStatus);
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentPersistenceService.save(shipment);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, shipment.getId());
    }

    public ShipmentResponse findCompanyShipment(AccountAuthentication authentication, Long shipmentId) {
        return shipmentSearchingService.findCompanyShipment(authentication.getCompany(), shipmentId);
    }

    public Page<ShipmentResponse> findCompanyShipments(AccountAuthentication authentication, String status, Long routeId, String dropUid, Pageable pageable) {
        return shipmentSearchingService.findCompanyShipments(authentication.getCompany(), status, routeId, dropUid, pageable);
    }
}
