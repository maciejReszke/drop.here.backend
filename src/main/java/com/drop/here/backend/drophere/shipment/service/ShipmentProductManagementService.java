package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.route.dto.RouteProductAmountChange;
import com.drop.here.backend.drophere.route.service.RouteProductService;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ShipmentProductManagementService {
    private final RouteProductService routeProductService;

    public void reduce(Shipment shipment) {
        changeAmount(shipment, false);
    }

    public void increase(Shipment shipment) {
        changeAmount(shipment, true);
    }

    private void changeAmount(Shipment shipment, boolean increase) {
        final List<RouteProductAmountChange> routeProductChanges = shipment.getProducts()
                .stream()
                .map(product -> routeProductAmountChange(product, increase))
                .collect(Collectors.toList());

        routeProductService.changeAmount(routeProductChanges);
    }

    private RouteProductAmountChange routeProductAmountChange(ShipmentProduct product, boolean increase) {
        return new RouteProductAmountChange(
                product.getRouteProduct(),
                increase ? product.getQuantity() : product.getQuantity().negate()
        );
    }

}
