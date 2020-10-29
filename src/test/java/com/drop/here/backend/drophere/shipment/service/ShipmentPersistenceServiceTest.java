package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentPersistenceServiceTest {
    @InjectMocks
    private ShipmentPersistenceService shipmentPersistenceService;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Test
    void givenExistingShipmentWhenFindShipmentByIdCustomerThenFind() {
        //given
        final Customer customer = Customer.builder().build();
        final long shipmentId = 5L;
        final Shipment shipment = Shipment.builder().build();

        when(shipmentRepository.findByIdAndCustomer(shipmentId, customer)).thenReturn(Optional.of(shipment));
        //when
        final Shipment result = shipmentPersistenceService.findShipment(shipmentId, customer);

        //then
        assertThat(result).isEqualTo(shipment);
    }

    @Test
    void givenNotExistingShipmentWhenFindShipmentByIdCustomerThenThrow() {
        //given
        final Customer customer = Customer.builder().build();
        final long shipmentId = 5L;

        when(shipmentRepository.findByIdAndCustomer(shipmentId, customer)).thenReturn(Optional.empty());
        //when
        final Throwable throwable = catchThrowable(() -> shipmentPersistenceService.findShipment(shipmentId, customer));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingShipmentWhenFindShipmentByIdCompanyThenFind() {
        //given
        final Company company = Company.builder().build();
        final long shipmentId = 5L;
        final Shipment shipment = Shipment.builder().build();

        when(shipmentRepository.findByIdAndCompany(shipmentId, company)).thenReturn(Optional.of(shipment));
        //when
        final Shipment result = shipmentPersistenceService.findShipment(shipmentId, company);

        //then
        assertThat(result).isEqualTo(shipment);
    }

    @Test
    void givenNotExistingShipmentWhenFindShipmentByIdCompanyThenThrow() {
        //given
        final Company company = Company.builder().build();
        final long shipmentId = 5L;

        when(shipmentRepository.findByIdAndCompany(shipmentId, company)).thenReturn(Optional.empty());
        //when
        final Throwable throwable = catchThrowable(() -> shipmentPersistenceService.findShipment(shipmentId, company));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

}