package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.product.dto.ProductCopy;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.enums.ProductCreationType;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.service.RouteProductService;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProductCalculation;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import com.drop.here.backend.drophere.test_data.ShipmentDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentProductMappingServiceTest {
    @InjectMocks
    private ShipmentProductMappingService shipmentProductMappingService;

    @Mock
    private RouteProductService routeProductService;

    @Mock
    private ProductService productService;

    @Mock
    private ShipmentCalculatingService shipmentCalculatingService;

    @Test
    void givenShipmentAndShipmentCustomerSubmissionRequestWhenCreateShipmentProductsThenCreate() {
        //given
        final Drop drop = Drop.builder().build();
        final Shipment shipment = Shipment.builder().drop(drop).build();
        final Company company = Company.builder().build();
        final ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest = ShipmentDataGenerator
                .customerSubmissionRequest(1);

        final RouteProduct routeProduct1 = RouteProduct.builder().id(7L)
                .product(Product.builder().id(5L).unitFraction(BigDecimal.ONE).build())
                .amount(BigDecimal.valueOf(44.42)).build();

        final RouteProduct routeProduct2 = RouteProduct.builder().id(8L)
                .product(Product.builder().id(6L).unitFraction(BigDecimal.ONE).build())
                .amount(BigDecimal.valueOf(45.43)).build();
        final ProductCustomization customization1 = ProductCustomization.builder().id(5L)
                .price(BigDecimal.valueOf(1232)).build();
        final ProductCustomization customization2 = ProductCustomization.builder().id(6L)
                .price(BigDecimal.valueOf(1422)).build();

        final ProductCustomizationWrapper wrapper1 = ProductCustomizationWrapper.builder()
                .customizations(Set.of(customization1))
                .build();

        final ProductCustomizationWrapper wrapper2 = ProductCustomizationWrapper.builder()
                .customizations(Set.of(customization2))
                .build();

        final Product productCopy1 = Product.builder().id(66L).unitFraction(BigDecimal.ONE).customizationWrappers(List.of(wrapper1)).build();
        final Product productCopy2 = Product.builder().id(67L).unitFraction(BigDecimal.ONE).customizationWrappers(List.of(wrapper2)).build();

        when(routeProductService.findProductsLocked(drop, Set.of(7L, 8L))).thenReturn(List.of(routeProduct1, routeProduct2));
        when(productService.createReadOnlyCopy(routeProduct1.getProduct().getId(), company, ProductCreationType.SHIPMENT))
                .thenReturn(new ProductCopy(routeProduct1.getProduct(), productCopy1));
        when(productService.createReadOnlyCopy(routeProduct2.getProduct().getId(), company, ProductCreationType.SHIPMENT))
                .thenReturn(new ProductCopy(routeProduct2.getProduct(), productCopy2));
        when(shipmentCalculatingService.calculateProductCost(any())).thenReturn(
                new ShipmentProductCalculation(BigDecimal.valueOf(55), BigDecimal.valueOf(66), BigDecimal.valueOf(77), BigDecimal.valueOf(78)),
                new ShipmentProductCalculation(BigDecimal.valueOf(88), BigDecimal.valueOf(99), BigDecimal.valueOf(101), BigDecimal.valueOf(102))
        );

        //when
        final Set<ShipmentProduct> products = shipmentProductMappingService.createShipmentProducts(shipment, company, shipmentCustomerSubmissionRequest);

        //then
        assertThat(products).hasSize(2);

        final ShipmentProduct firstProduct = products.stream().filter(p -> p.getOrderNum().equals(1)).findFirst().orElseThrow();
        assertThat(firstProduct.getShipment()).isEqualTo(shipment);
        assertThat(firstProduct.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(firstProduct.getUnitCustomizationsPrice()).isEqualTo(BigDecimal.valueOf(66));
        assertThat(firstProduct.getCustomizations()).hasSize(1);
        final ShipmentProductCustomization firstCustomization = firstProduct.getCustomizations().stream()
                .findFirst().orElseThrow();
        assertThat(firstCustomization.getPrice()).isEqualByComparingTo(customization1.getPrice());
        assertThat(firstCustomization.getProductCustomization()).isEqualTo(customization1);
        assertThat(firstCustomization.getShipmentProduct()).isNotNull();
        assertThat(firstProduct.getOrderNum()).isEqualTo(1);
        assertThat(firstProduct.getQuantity()).isEqualTo(shipmentCustomerSubmissionRequest.getProducts().get(0).getQuantity());
        assertThat(firstProduct.getSummarizedPrice()).isEqualTo(BigDecimal.valueOf(78));
        assertThat(firstProduct.getUnitPrice()).isEqualTo(BigDecimal.valueOf(55));
        assertThat(firstProduct.getUnitSummarizedPrice()).isEqualTo(BigDecimal.valueOf(77));
        assertThat(firstProduct.getProduct()).isEqualTo(productCopy1);
        assertThat(firstProduct.getRouteProduct()).isEqualTo(routeProduct1);

        final ShipmentProduct secondProduct = products.stream().filter(p -> p.getOrderNum().equals(2)).findFirst().orElseThrow();
        assertThat(secondProduct.getShipment()).isEqualTo(shipment);
        assertThat(secondProduct.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(secondProduct.getUnitCustomizationsPrice()).isEqualTo(BigDecimal.valueOf(99));
        assertThat(secondProduct.getCustomizations()).hasSize(1);
        final ShipmentProductCustomization secondCustomization = secondProduct.getCustomizations().stream()
                .findFirst().orElseThrow();
        assertThat(secondCustomization.getPrice()).isEqualByComparingTo(customization2.getPrice());
        assertThat(secondCustomization.getProductCustomization()).isEqualTo(customization2);
        assertThat(secondCustomization.getShipmentProduct()).isNotNull();
        assertThat(secondProduct.getOrderNum()).isEqualTo(2);
        assertThat(secondProduct.getQuantity()).isEqualTo(shipmentCustomerSubmissionRequest.getProducts().get(0).getQuantity());
        assertThat(secondProduct.getSummarizedPrice()).isEqualTo(BigDecimal.valueOf(102));
        assertThat(secondProduct.getUnitSummarizedPrice()).isEqualTo(BigDecimal.valueOf(101));
        assertThat(secondProduct.getUnitPrice()).isEqualTo(BigDecimal.valueOf(88));
        assertThat(secondProduct.getProduct()).isEqualTo(productCopy2);
        assertThat(secondProduct.getRouteProduct()).isEqualTo(routeProduct2);
    }

}