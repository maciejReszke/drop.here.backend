package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.service.RouteProductService;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomizationRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProductCalculation;
import com.drop.here.backend.drophere.shipment.dto.ShipmentProductRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentProductMappingService {
    private final RouteProductService routeProductService;
    private final ShipmentCalculatingService shipmentCalculatingService;

    public Set<ShipmentProduct> createShipmentProducts(Shipment shipment, ShipmentCustomerSubmissionRequest request) {
        final List<RouteProduct> products = getRouteProducts(shipment, request);
        final AtomicInteger counter = new AtomicInteger(0);

        return request.getProducts().stream()
                .map(productRequest -> toShipmentProduct(
                        productRequest,
                        findProduct(productRequest.getRouteProductId(), products),
                        shipment,
                        counter.incrementAndGet()
                ))
                .collect(Collectors.toSet());
    }

    private List<RouteProduct> getRouteProducts(Shipment shipment, ShipmentCustomerSubmissionRequest request) {
        final Set<Long> routeProductsIds = request.getProducts()
                .stream()
                .map(ShipmentProductRequest::getRouteProductId)
                .collect(Collectors.toSet());

        return routeProductService.findProductsLocked(shipment.getDrop(), routeProductsIds);
    }

    private ShipmentProduct toShipmentProduct(ShipmentProductRequest productRequest, RouteProduct product, Shipment shipment, int orderNum) {
        final ShipmentProduct shipmentProduct = buildBaseShipmentProduct(productRequest, product, shipment, orderNum);
        setCustomizations(productRequest, product.getProduct(), shipmentProduct);
        updatePrice(shipmentProduct);
        return shipmentProduct;
    }

    private void setCustomizations(ShipmentProductRequest productRequest, Product product, ShipmentProduct shipmentProduct) {
        shipmentProduct.setCustomizations(productRequest.getCustomizations().stream()
                .map(customization -> toShipmentProductCustomization(shipmentProduct, findCustomization(customization, product)))
                .collect(Collectors.toSet()));
    }

    private void updatePrice(ShipmentProduct shipmentProduct) {
        final ShipmentProductCalculation calculation = shipmentCalculatingService.calculateProductCost(shipmentProduct);
        shipmentProduct.setUnitCustomizationsPrice(calculation.getUnitCustomizationsPrice());
        shipmentProduct.setUnitPrice(calculation.getUnitPrice());
        shipmentProduct.setSummarizedPrice(calculation.getSummarizedPrice());
        shipmentProduct.setUnitSummarizedPrice(calculation.getUnitSummarizedPrice());
    }

    private ShipmentProduct buildBaseShipmentProduct(ShipmentProductRequest productRequest, RouteProduct routeProduct, Shipment shipment, int orderNum) {
        return ShipmentProduct.builder()
                .product(routeProduct.getProduct())
                .routeProduct(routeProduct)
                .shipment(shipment)
                .orderNum(orderNum)
                .quantity(productRequest.getQuantity())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ShipmentProductCustomization toShipmentProductCustomization(ShipmentProduct shipmentProduct, ProductCustomization customization) {
        return ShipmentProductCustomization.builder()
                .productCustomization(customization)
                .shipmentProduct(shipmentProduct)
                .price(customization.getPrice())
                .build();
    }

    private ProductCustomization findCustomization(ShipmentCustomizationRequest customizationRequest, Product product) {
        return product.getCustomizationWrappers().stream()
                .map(ProductCustomizationWrapper::getCustomizations)
                .flatMap(Collection::stream)
                .filter(customization -> customization.getId().equals(customizationRequest.getId()))
                .findFirst()
                .orElseThrow();
    }

    private RouteProduct findProduct(Long routeProductId, List<RouteProduct> products) {
        return products.stream()
                .filter(routeProduct -> routeProduct.getId().equals(routeProductId))
                .findFirst()
                .orElseThrow();
    }
}
