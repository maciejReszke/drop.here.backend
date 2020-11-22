package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerSearchingService;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.service.DropSearchingService;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.service.ProductCustomizationService;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.shipment.dto.ShipmentResponse;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.repository.ShipmentProductCustomizationRepository;
import com.drop.here.backend.drophere.shipment.repository.ShipmentProductRepository;
import com.drop.here.backend.drophere.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentSearchingServiceTest {

    @InjectMocks
    private ShipmentSearchingService shipmentSearchingService;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentProductRepository productRepository;

    @Mock
    private DropSearchingService dropSearchingService;

    @Mock
    private CompanyService companyService;

    @Mock
    private ShipmentProductCustomizationRepository shipmentProductCustomizationRepository;

    @Mock
    private ProductCustomizationService productCustomizationService;

    @Mock
    private ShipmentPersistenceService shipmentPersistenceService;

    @Mock
    private CustomerSearchingService customerSearchingService;

    @Test
    void givenValidStatusWhenFindCustomerShipmentsThenFind() {
        //given
        final Customer customer = Customer.builder().id(8L).build();
        final String status = ShipmentStatus.ACCEPTED.name();
        final Pageable pageable = Pageable.unpaged();
        final Drop drop = Drop.builder().id(1L).build();
        final Company company = Company.builder().id(2L).build();
        final ProductCustomization customization = ProductCustomization.builder().id(7L)
                .wrapper(ProductCustomizationWrapper.builder().build())
                .build();
        final ShipmentProductCustomization shipmentProductCustomization = ShipmentProductCustomization.builder().id(5L)
                .productCustomization(customization)
                .build();
        final Product product = Product.builder().id(6L).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .id(4L)
                .product(product)
                .routeProduct(RouteProduct.builder().build())
                .customizations(Set.of(shipmentProductCustomization))
                .build();
        final Shipment shipment = Shipment.builder()
                .id(3L)
                .drop(drop)
                .company(company)
                .customer(customer)
                .products(Set.of(shipmentProduct))
                .build();
        shipmentProduct.setShipment(shipment);
        shipmentProductCustomization.setShipmentProduct(shipmentProduct);

        when(shipmentRepository.findByCustomerAndStatus(customer, ShipmentStatus.ACCEPTED, pageable))
                .thenReturn(new PageImpl<>(List.of(shipment)));
        when(dropSearchingService.findDrops(List.of(drop.getId()))).thenReturn(List.of(drop));
        when(companyService.findCompanies(List.of(2L))).thenReturn(List.of(company));
        when(productRepository.findByIdWithProduct(List.of(shipmentProduct.getId()))).thenReturn(List.of(shipmentProduct));
        when(shipmentProductCustomizationRepository.findAllById(List.of(5L))).thenReturn(List.of(shipmentProductCustomization));
        when(productCustomizationService.findCustomizationsWithWrapper(List.of(7L))).thenReturn(List.of(customization));
        when(customerSearchingService.findCustomers(List.of(8L))).thenReturn(List.of(customer));

        //when
        final Page<ShipmentResponse> result = shipmentSearchingService.findCustomerShipments(customer, status, pageable);

        //then
        assert result != null;
    }

    @Test
    void givenValidIdWhenFindCustomerShipmentThenFind() {
        //given
        final Customer customer = Customer.builder().id(8L).build();
        final Drop drop = Drop.builder().id(1L).build();
        final Company company = Company.builder().id(2L).build();
        final ProductCustomization customization = ProductCustomization.builder().id(7L)
                .wrapper(ProductCustomizationWrapper.builder().build())
                .build();
        final ShipmentProductCustomization shipmentProductCustomization = ShipmentProductCustomization.builder().id(5L)
                .productCustomization(customization)
                .build();
        final Product product = Product.builder().id(6L).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .id(4L)
                .product(product)
                .routeProduct(RouteProduct.builder().build())
                .customizations(Set.of(shipmentProductCustomization))
                .build();
        final Shipment shipment = Shipment.builder()
                .id(3L)
                .drop(drop)
                .customer(customer)
                .company(company)
                .products(Set.of(shipmentProduct))
                .build();
        shipmentProduct.setShipment(shipment);
        shipmentProductCustomization.setShipmentProduct(shipmentProduct);

        when(shipmentPersistenceService.findShipment(1L, customer)).thenReturn(shipment);
        when(dropSearchingService.findDrops(List.of(drop.getId()))).thenReturn(List.of(drop));
        when(companyService.findCompanies(List.of(2L))).thenReturn(List.of(company));
        when(productRepository.findByIdWithProduct(List.of(shipmentProduct.getId()))).thenReturn(List.of(shipmentProduct));
        when(shipmentProductCustomizationRepository.findAllById(List.of(5L))).thenReturn(List.of(shipmentProductCustomization));
        when(productCustomizationService.findCustomizationsWithWrapper(List.of(7L))).thenReturn(List.of(customization));
        when(customerSearchingService.findCustomers(List.of(8L))).thenReturn(List.of(customer));
        //when
        final ShipmentResponse result = shipmentSearchingService.findCustomerShipment(customer, 1L);

        //then
        assert result != null;
    }

    @Test
    void givenValidIdWhenFindCompanyShipmentThenFind() {
        //given
        final Customer customer = Customer.builder().id(8L).build();
        final Drop drop = Drop.builder().id(1L).build();
        final Company company = Company.builder().id(2L).build();
        final ProductCustomization customization = ProductCustomization.builder().id(7L)
                .wrapper(ProductCustomizationWrapper.builder().build())
                .build();
        final ShipmentProductCustomization shipmentProductCustomization = ShipmentProductCustomization.builder().id(5L)
                .productCustomization(customization)
                .build();
        final Product product = Product.builder().id(6L).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .id(4L)
                .product(product)
                .routeProduct(RouteProduct.builder().build())
                .customizations(Set.of(shipmentProductCustomization))
                .build();
        final Shipment shipment = Shipment.builder()
                .id(3L)
                .drop(drop)
                .customer(customer)
                .company(company)
                .products(Set.of(shipmentProduct))
                .build();
        shipmentProduct.setShipment(shipment);
        shipmentProductCustomization.setShipmentProduct(shipmentProduct);

        when(shipmentPersistenceService.findShipment(1L, company)).thenReturn(shipment);
        when(dropSearchingService.findDrops(List.of(drop.getId()))).thenReturn(List.of(drop));
        when(companyService.findCompanies(List.of(2L))).thenReturn(List.of(company));
        when(productRepository.findByIdWithProduct(List.of(shipmentProduct.getId()))).thenReturn(List.of(shipmentProduct));
        when(shipmentProductCustomizationRepository.findAllById(List.of(5L))).thenReturn(List.of(shipmentProductCustomization));
        when(productCustomizationService.findCustomizationsWithWrapper(List.of(7L))).thenReturn(List.of(customization));
        when(customerSearchingService.findCustomers(List.of(8L))).thenReturn(List.of(customer));
        //when
        final ShipmentResponse result = shipmentSearchingService.findCompanyShipment(company, 1L);

        //then
        assert result != null;
    }

    @Test
    void givenValidStatusWhenFindCompanyShipmentsThenFind() {
        //given
        final Customer customer = Customer.builder().id(8L).build();
        final String status = ShipmentStatus.ACCEPTED.name();
        final Long routeId = 5L;
        final String dropUid = "dropUid";
        final Pageable pageable = Pageable.unpaged();
        final Drop drop = Drop.builder().id(1L).build();
        final Company company = Company.builder().id(2L).build();
        final ProductCustomization customization = ProductCustomization.builder().id(7L)
                .wrapper(ProductCustomizationWrapper.builder().build())
                .build();
        final ShipmentProductCustomization shipmentProductCustomization = ShipmentProductCustomization.builder().id(5L)
                .productCustomization(customization)
                .build();
        final Product product = Product.builder().id(6L).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .id(4L)
                .routeProduct(RouteProduct.builder().build())
                .product(product)
                .customizations(Set.of(shipmentProductCustomization))
                .build();
        final Shipment shipment = Shipment.builder()
                .id(3L)
                .drop(drop)
                .company(company)
                .customer(customer)
                .products(Set.of(shipmentProduct))
                .build();
        shipmentProduct.setShipment(shipment);
        shipmentProductCustomization.setShipmentProduct(shipmentProduct);

        when(shipmentRepository.findByCompanyAndStatusAndRouteIdAndDropUid(company, ShipmentStatus.ACCEPTED, routeId, dropUid, pageable))
                .thenReturn(new PageImpl<>(List.of(shipment)));
        when(dropSearchingService.findDrops(List.of(drop.getId()))).thenReturn(List.of(drop));
        when(companyService.findCompanies(List.of(2L))).thenReturn(List.of(company));
        when(productRepository.findByIdWithProduct(List.of(shipmentProduct.getId()))).thenReturn(List.of(shipmentProduct));
        when(shipmentProductCustomizationRepository.findAllById(List.of(5L))).thenReturn(List.of(shipmentProductCustomization));
        when(productCustomizationService.findCustomizationsWithWrapper(List.of(7L))).thenReturn(List.of(customization));
        when(customerSearchingService.findCustomers(List.of(8L))).thenReturn(List.of(customer));

        //when
        final Page<ShipmentResponse> result = shipmentSearchingService.findCompanyShipments(company, status, routeId, dropUid, pageable);

        //then
        assert result != null;
    }

}