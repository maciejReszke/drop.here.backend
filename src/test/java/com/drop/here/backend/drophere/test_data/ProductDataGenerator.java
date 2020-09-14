package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ProductDataGenerator {
    public Product product(int i, ProductUnit unit, Company company) {
        return Product.builder()
                .name("productName" + i)
                .category("category" + i)
                .unit(unit)
                .unitName(unit.getName())
                .unitFraction(BigDecimal.valueOf(15.12))
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE)
                .price(BigDecimal.valueOf(123 + i))
                .description("description" + i)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .deletable(true)
                .company(company)
                .build();
    }

    public ProductUnit unit(int i) {
        return ProductUnit.builder()
                .createdAt(LocalDateTime.now())
                .name("unit" + i)
                .build();
    }

    public ProductManagementRequest managementRequest(int i) {
        return ProductManagementRequest.builder()
                .unit("unit" + i)
                .availabilityStatus(ProductAvailabilityStatus.UNAVAILABLE.name())
                .category("category" + i)
                .price(BigDecimal.valueOf(55.1 + i))
                .description("description" + i)
                .name("name" + i)
                .build();
    }

    public ProductCustomizationWrapperRequest productCustomizationWrapperRequest(int i) {
        return ProductCustomizationWrapperRequest.builder()
                .customizations(List.of(productCustomizationRequest(2 * i), productCustomizationRequest(2 * i + 1)))
                .heading("heading" + i)
                .type(ProductCustomizationWrapperType.SINGLE.name())
                .build();
    }

    private ProductCustomizationRequest productCustomizationRequest(int i) {
        return ProductCustomizationRequest.builder()
                .price(BigDecimal.valueOf(55.44 + i))
                .value("customizationName" + i)
                .build();
    }

    public ProductCustomizationWrapper productCustomizationWrapper(int i, Product product) {
        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder()
                .heading("header" + i)
                .product(product)
                .type(ProductCustomizationWrapperType.SINGLE)
                .build();

        final ProductCustomization customization = ProductCustomization.builder()
                .orderNum(i)
                .price(BigDecimal.valueOf(2 + i))
                .value("value" + i)
                .wrapper(wrapper)
                .build();

        wrapper.setCustomizations(Set.of(customization));
        return wrapper;
    }
}
