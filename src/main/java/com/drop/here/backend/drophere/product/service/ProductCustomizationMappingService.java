package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ProductCustomizationMappingService {

    public ProductCustomizationWrapper toCustomizationWrapper(Product product, ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        final ProductCustomizationWrapper productCustomizationWrapper = mapToCustomizationWrapper(product, productCustomizationWrapperRequest);

        final AtomicInteger counter = new AtomicInteger(0);
        final Set<ProductCustomization> customizations = productCustomizationWrapperRequest.getCustomizations()
                .stream()
                .map(customization -> toCustomization(customization, productCustomizationWrapper, counter.incrementAndGet()))
                .collect(Collectors.toSet());

        productCustomizationWrapper.setCustomizations(customizations);

        return productCustomizationWrapper;
    }

    private ProductCustomizationWrapper mapToCustomizationWrapper(Product product, ProductCustomizationWrapperRequest request) {
        return ProductCustomizationWrapper.builder()
                .heading(request.getHeading())
                .product(product)
                .type(ProductCustomizationWrapperType.valueOf(request.getType()))
                .build();
    }

    private ProductCustomization toCustomization(ProductCustomizationRequest customization, ProductCustomizationWrapper wrapper, int order) {
        return ProductCustomization.builder()
                .price(customization.getPrice().setScale(2, RoundingMode.DOWN))
                .value(customization.getValue())
                .order(order)
                .wrapper(wrapper)
                .build();
    }
}
