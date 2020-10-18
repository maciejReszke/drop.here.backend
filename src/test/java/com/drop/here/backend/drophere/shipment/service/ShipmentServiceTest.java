package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentCustomerDecision;
import com.drop.here.backend.drophere.shipment.enums.ShipmentProcessOperation;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingServiceFactory;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import com.drop.here.backend.drophere.test_data.ShipmentDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @InjectMocks
    private ShipmentService shipmentService;

    @Mock
    private ShipmentValidationService shipmentValidationService;

    @Mock
    private ShipmentMappingService shipmentMappingService;

    @Mock
    private DropService dropService;

    @Mock
    private ShipmentProcessingServiceFactory shipmentProcessingServiceFactory;

    @Mock
    private ShipmentPersistenceService shipmentPersistenceService;

    @Mock
    private ShipmentSearchingService shipmentSearchingService;

    @Test
    void givenShipmentRequestWhenCreateShipmentThenCreate() {
        //given
        final String dropUid = "dropUid";
        final ShipmentCustomerSubmissionRequest submissionRequest = ShipmentDataGenerator.customerSubmissionRequest(1);
        final Account account = AccountDataGenerator.customerAccount(1);
        final Customer customer = CustomerDataGenerator.customer(1, account);
        account.setCustomer(customer);
        final AccountAuthentication authentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Drop drop = DropDataGenerator.drop(1, null, null);
        final Company company = Company.builder().build();
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, Set.of());
        shipment.setStatus(null);

        when(dropService.findPrivilegedDrop(dropUid, customer)).thenReturn(drop);
        when(shipmentMappingService.toEntity(drop, submissionRequest, customer)).thenReturn(shipment);
        doNothing().when(shipmentValidationService).validateShipment(shipment);
        when(shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerSubmission(submissionRequest), ShipmentProcessOperation.NEW))
                .thenReturn(ShipmentStatus.PLACED);
        doNothing().when(shipmentPersistenceService).save(shipment);

        //when
        final ResourceOperationResponse result = shipmentService.createShipment(dropUid, submissionRequest, authentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
    }

    @Test
    void givenShipmentRequestWhenUpdateShipmentThenUpdate() {
        //given
        final ShipmentCustomerSubmissionRequest submissionRequest = ShipmentDataGenerator.customerSubmissionRequest(1);
        final Account account = AccountDataGenerator.customerAccount(1);
        final Customer customer = CustomerDataGenerator.customer(1, account);
        account.setCustomer(customer);
        final AccountAuthentication authentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Drop drop = DropDataGenerator.drop(1, null, null);
        final Company company = Company.builder().build();
        final long shipmentId = 5L;
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, Set.of());
        shipment.setUpdatedAt(null);
        shipment.setStatus(null);

        when(shipmentPersistenceService.findShipment(shipmentId, customer)).thenReturn(shipment);
        doNothing().when(shipmentValidationService).validateShipment(shipment);
        doNothing().when(shipmentValidationService).validateShipmentCustomerUpdate(shipment);
        doNothing().when(shipmentMappingService).update(shipment, submissionRequest);
        when(shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerSubmission(submissionRequest), ShipmentProcessOperation.BY_CUSTOMER_UPDATED))
                .thenReturn(ShipmentStatus.PLACED);
        doNothing().when(shipmentPersistenceService).save(shipment);

        //when
        final ResourceOperationResponse result = shipmentService.update(shipmentId, submissionRequest, authentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(shipment.getUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }

    @Test
    void givenShipmentRequestWhenUpdateShipmentStatusThenUpdate() {
        //given
        final ShipmentCustomerDecisionRequest shipmentCustomerDecisionRequest = ShipmentCustomerDecisionRequest.builder()
                .customerDecision(ShipmentCustomerDecision.ACCEPT)
                .build();
        final Account account = AccountDataGenerator.customerAccount(1);
        final Customer customer = CustomerDataGenerator.customer(1, account);
        account.setCustomer(customer);
        final AccountAuthentication authentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Drop drop = DropDataGenerator.drop(1, null, null);
        final Company company = Company.builder().build();
        final long shipmentId = 5L;
        final Shipment shipment = ShipmentDataGenerator.shipment(1, drop, company, customer, Set.of());
        shipment.setUpdatedAt(null);

        when(shipmentPersistenceService.findShipment(shipmentId, customer)).thenReturn(shipment);
        when(shipmentProcessingServiceFactory.process(shipment, ShipmentProcessingRequest.customerDecision(shipmentCustomerDecisionRequest), ShipmentProcessOperation.CUSTOMER_DECISION))
                .thenReturn(ShipmentStatus.PLACED);
        doNothing().when(shipmentPersistenceService).save(shipment);


        //when
        final ResourceOperationResponse result = shipmentService.updateShipmentStatus(shipmentId, shipmentCustomerDecisionRequest, authentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.PLACED);
        assertThat(shipment.getUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }


}