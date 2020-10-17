package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProductCalculation;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShipmentCalculatingServiceTest {

    @InjectMocks
    private ShipmentCalculatingService shipmentCalculatingService;

    @Test
    void givenShipmentProductWithCustomizationsWhenCalculateProductCostThenCalculate() {
        //given
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .customizations(Set.of(ShipmentProductCustomization.builder()
                                .price(BigDecimal.valueOf(44.33))
                                .build(),
                        ShipmentProductCustomization.builder()
                                .price(BigDecimal.valueOf(24.19))
                                .build()))
                .unitPrice(BigDecimal.valueOf(55.33))
                .build();

        //when
        final ShipmentProductCalculation result = shipmentCalculatingService.calculateProductCost(shipmentProduct);

        //then
        assertThat(result.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(55.33));
        assertThat(result.getCustomizationsPrice()).isEqualByComparingTo(BigDecimal.valueOf(68.52));
        assertThat(result.getSummarizedPrice()).isEqualByComparingTo(BigDecimal.valueOf(123.85));
    }

    @Test
    void givenShipmentProductWithoutCustomizationsWhenCalculateProductCostThenCalculate() {
        //given
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .customizations(Set.of())
                .unitPrice(BigDecimal.valueOf(55.33))
                .build();

        //when
        final ShipmentProductCalculation result = shipmentCalculatingService.calculateProductCost(shipmentProduct);

        //then
        assertThat(result.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(55.33));
        assertThat(result.getCustomizationsPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getSummarizedPrice()).isEqualByComparingTo(BigDecimal.valueOf(55.33));
    }

    @Test
    void givenShipmentWhenCalculateShipmentThenCalculate() {
        //given
        final Shipment shipment = Shipment.builder()
                .products(Set.of(
                        ShipmentProduct.builder()
                                .summarizedPrice(BigDecimal.valueOf(182.11))
                                .build(),
                        ShipmentProduct.builder()
                                .summarizedPrice(BigDecimal.valueOf(912.29))
                                .build()
                ))
                .build();


        //when
        final BigDecimal result = shipmentCalculatingService.calculateShipment(shipment);

        //then
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1094.40));
    }
}