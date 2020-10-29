package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.route.dto.RouteProductAmountChange;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.service.RouteProductService;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ShipmentProductManagementServiceTest {

    @InjectMocks
    private ShipmentProductManagementService shipmentProductManagementService;

    @Mock
    private RouteProductService routeProductService;

    @Test
    void givenShipmentWhenReduceThenChangeAmount() {
        //given
        final RouteProduct routeProduct = RouteProduct.builder().build();
        final Shipment shipment = Shipment.builder()
                .products(Set.of(ShipmentProduct.builder()
                        .routeProduct(routeProduct)
                        .quantity(BigDecimal.valueOf(33.45))
                        .build()))
                .build();

        doNothing().when(routeProductService).changeAmount(List.of(new RouteProductAmountChange(
                routeProduct, BigDecimal.valueOf(-33.45))));
        //when
        shipmentProductManagementService.reduce(shipment);

        //then
        verifyNoMoreInteractions(routeProductService);
    }

    @Test
    void givenShipmentWhenIncreaseThenChangeAmount() {
        //given
        final RouteProduct routeProduct = RouteProduct.builder().build();
        final Shipment shipment = Shipment.builder()
                .products(Set.of(ShipmentProduct.builder()
                        .routeProduct(routeProduct)
                        .quantity(BigDecimal.valueOf(33.45))
                        .build()))
                .build();

        doNothing().when(routeProductService).changeAmount(List.of(new RouteProductAmountChange(
                routeProduct, BigDecimal.valueOf(33.45))));
        //when
        shipmentProductManagementService.increase(shipment);

        //then
        verifyNoMoreInteractions(routeProductService);
    }
}