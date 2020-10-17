package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.ProductCopy;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.enums.ProductCreationType;
import com.drop.here.backend.drophere.product.service.ProductCustomizationService;
import com.drop.here.backend.drophere.product.service.ProductService;
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
    private final ProductService productService;
    private final ProductCustomizationService productCustomizationService;
    private final ShipmentCalculatingService shipmentCalculatingService;

    // TODO: 17/10/2020 test
    public Set<ShipmentProduct> createShipmentProducts(Shipment shipment, Company company, ShipmentCustomerSubmissionRequest request) {
        final List<RouteProduct> products = getRouteProducts(shipment, request);
        final List<ProductCustomization> customizations = getCustomizations(products);
        final AtomicInteger counter = new AtomicInteger(0);

        return request.getProducts().stream()
                .map(productRequest -> toShipmentProduct(
                        productRequest,
                        company,
                        findProduct(productRequest.getRouteProductId(), products),
                        customizations,
                        shipment,
                        counter.incrementAndGet()
                ))
                .collect(Collectors.toSet());
    }

    private List<ProductCustomization> getCustomizations(List<RouteProduct> products) {
        final List<Long> productsIds = products.stream()
                .map(RouteProduct::getProduct)
                .map(Product::getId)
                .collect(Collectors.toList());

        return productCustomizationService.findCustomizations(productsIds)
                .stream()
                .map(ProductCustomizationWrapper::getCustomizations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<RouteProduct> getRouteProducts(Shipment shipment, ShipmentCustomerSubmissionRequest request) {
        final Set<Long> routeProductsIds = request.getProducts()
                .stream()
                .map(ShipmentProductRequest::getRouteProductId)
                .collect(Collectors.toSet());

        return routeProductService.findProducts(shipment.getDrop(), routeProductsIds);
    }

    private ShipmentProduct toShipmentProduct(ShipmentProductRequest productRequest, Company company, RouteProduct product, List<ProductCustomization> customizations, Shipment shipment, int orderNum) {
        final ProductCopy productCopy = productService.createReadOnlyCopy(product.getProduct().getId(), company, ProductCreationType.SHIPMENT);
        final ShipmentProduct shipmentProduct = buildBaseShipmentProduct(productRequest, product, shipment, orderNum, productCopy);
        setCustomizations(productRequest, product, customizations, shipmentProduct);
        updatePrice(shipmentProduct);
        return shipmentProduct;
    }

    private void setCustomizations(ShipmentProductRequest productRequest, RouteProduct product, List<ProductCustomization> customizations, ShipmentProduct shipmentProduct) {
        shipmentProduct.setCustomizations(productRequest.getCustomizations().stream()
                .map(customization -> toShipmentProductCustomization(shipmentProduct, findCustomization(customization, product.getProduct(), customizations)))
                .collect(Collectors.toSet()));
    }

    private void updatePrice(ShipmentProduct shipmentProduct) {
        final ShipmentProductCalculation calculation = shipmentCalculatingService.calculateProductCost(shipmentProduct);
        shipmentProduct.setCustomizationsPrice(calculation.getCustomizationsPrice());
        shipmentProduct.setUnitPrice(calculation.getUnitPrice());
        shipmentProduct.setSummarizedPrice(calculation.getSummarizedPrice());
    }

    private ShipmentProduct buildBaseShipmentProduct(ShipmentProductRequest productRequest, RouteProduct product, Shipment shipment, int orderNum, ProductCopy productCopy) {
        return ShipmentProduct.builder()
                .product(productCopy.getCopy())
                .routeProduct(product)
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

    private ProductCustomization findCustomization(ShipmentCustomizationRequest customizationRequest, Product product, List<ProductCustomization> customizations) {
        return customizations.stream()
                .filter(customization -> matchesCustomization(customizationRequest, product, customization))
                .findFirst()
                .orElseThrow();
    }

    private boolean matchesCustomization(ShipmentCustomizationRequest customizationRequest, Product product, ProductCustomization customization) {
        return customization.getId().equals(customizationRequest.getId()) &&
                customization.getWrapper().getProduct().getId().equals(product.getId());
    }

    private RouteProduct findProduct(Long routeProductId, List<RouteProduct> products) {
        return products.stream()
                .filter(routeProduct -> routeProduct.getId().equals(routeProductId))
                .findFirst()
                .orElseThrow();
    }
}
