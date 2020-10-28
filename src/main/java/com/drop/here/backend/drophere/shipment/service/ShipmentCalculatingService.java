package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProductCalculation;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ShipmentCalculatingService {

    public ShipmentProductCalculation calculateProductCost(ShipmentProduct shipmentProduct) {
        final BigDecimal customizationsPrice = shipmentProduct.getCustomizations().stream()
                .map(ShipmentProductCustomization::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal productPrice = shipmentProduct.getRouteProduct().getPrice();
        final BigDecimal unitCustomizedPrice = customizationsPrice.add(productPrice);

        return new ShipmentProductCalculation(
                scale(productPrice),
                scale(customizationsPrice),
                scale(unitCustomizedPrice),
                scale(unitCustomizedPrice.multiply(shipmentProduct.getQuantity()))
        );
    }

    private BigDecimal scale(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal calculateShipment(Shipment shipment) {
        final BigDecimal amount = shipment.getProducts().stream()
                .map(ShipmentProduct::getSummarizedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return scale(amount);
    }
}
