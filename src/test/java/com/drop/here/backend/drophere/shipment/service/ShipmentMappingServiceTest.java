package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import com.drop.here.backend.drophere.test_data.RouteDataGenerator;
import com.drop.here.backend.drophere.test_data.ShipmentDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentMappingServiceTest {

    @InjectMocks
    private ShipmentMappingService shipmentMappingService;

    @Mock
    private ShipmentCalculatingService shipmentCalculatingService;

    @Mock
    private ShipmentProductMappingService shipmentProductMappingService;

    @Test
    void givenShipmentCustomerSubmissionRequestWhenToEntityThenMap() {
        //given
        final Account account = AccountDataGenerator.customerAccount(1);
        final Country country = CountryDataGenerator.poland();
        final Company company = CompanyDataGenerator.company(1, account, country);
        final Route route = RouteDataGenerator.route(1, company);
        final Spot spot = SpotDataGenerator.spot(1, company);
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        final Customer customer = CustomerDataGenerator.customer(1, account);
        final ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest =
                ShipmentDataGenerator.customerSubmissionRequest(1);

        final Set<ShipmentProduct> products = Set.of(ShipmentProduct.builder().build());
        when(shipmentCalculatingService.calculateShipment(any())).thenReturn(BigDecimal.valueOf(44.32));
        when(shipmentProductMappingService.createShipmentProducts(any(), eq(shipmentCustomerSubmissionRequest)))
                .thenReturn(products);
        //when
        final Shipment shipment = shipmentMappingService.toEntity(drop, shipmentCustomerSubmissionRequest, customer);

        //then
        assertThat(shipment.getProducts()).isEqualTo(products);
        assertThat(shipment.getDrop()).isEqualTo(drop);
        assertThat(shipment.getCompany()).isEqualTo(company);
        assertThat(shipment.getCompanyComment()).isNull();
        assertThat(shipment.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(shipment.getCustomer()).isEqualTo(customer);
        assertThat(shipment.getCustomerComment()).isEqualTo(shipmentCustomerSubmissionRequest.getComment());
        assertThat(shipment.getStatus()).isNull();
        assertThat(shipment.getSummarizedAmount()).isEqualByComparingTo(BigDecimal.valueOf(44.32));
        assertThat(shipment.getUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
    }

    @Test
    void givenShipmentCustomerSubmissionRequestWhenUpdateThenUpdate() {
        //given
        final Account account = AccountDataGenerator.customerAccount(1);
        final Country country = CountryDataGenerator.poland();
        final Company company = CompanyDataGenerator.company(1, account, country);
        final Route route = RouteDataGenerator.route(1, company);
        final Spot spot = SpotDataGenerator.spot(1, company);
        final Drop drop = DropDataGenerator.drop(1, route, spot);
        final ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest =
                ShipmentDataGenerator.customerSubmissionRequest(1);
        final ShipmentProduct previousShipmentProduct = ShipmentProduct.builder().shipment(Shipment.builder().build()).build();
        final Shipment shipment = Shipment.builder().drop(drop).products(new LinkedHashSet<>(Set.of(previousShipmentProduct)))
                .company(company).build();
        final Set<ShipmentProduct> products = Set.of(ShipmentProduct.builder().build());
        when(shipmentCalculatingService.calculateShipment(any())).thenReturn(BigDecimal.valueOf(44.32));
        when(shipmentProductMappingService.createShipmentProducts(any(), eq(shipmentCustomerSubmissionRequest)))
                .thenReturn(products);
        //when
        shipmentMappingService.update(shipment, shipmentCustomerSubmissionRequest);

        //then
        assertThat(shipment.getProducts()).isEqualTo(products);
        assertThat(shipment.getDrop()).isEqualTo(drop);
        assertThat(shipment.getCompany()).isEqualTo(company);
        assertThat(shipment.getCompanyComment()).isNull();
        assertThat(shipment.getCreatedAt()).isNull();
        assertThat(shipment.getCustomer()).isNull();
        assertThat(shipment.getCustomerComment()).isEqualTo(shipmentCustomerSubmissionRequest.getComment());
        assertThat(shipment.getStatus()).isNull();
        assertThat(shipment.getSummarizedAmount()).isEqualByComparingTo(BigDecimal.valueOf(44.32));
        assertThat(shipment.getUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(previousShipmentProduct.getShipment()).isNull();
    }

}