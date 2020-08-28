package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class
ProductDataGenerator {
    public Product product(int i, ProductCategory category, ProductUnit unit, Company company) {
        return Product.builder()
                .name("productName" + i)
                .category(category)
                .categoryName(category.getName())
                .unit(unit)
                .unitName(unit.getName())
                .unitValue(BigDecimal.valueOf(15.12))
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE)
                .price(BigDecimal.valueOf(123 + i))
                .description("description" + i)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .deletable(false)
                .company(company)
                .build();
    }

    public ProductUnit unit(int i) {
        return ProductUnit.builder()
                .createdAt(LocalDateTime.now())
                .name("unit" + i)
                .build();
    }

    public ProductCategory category(int i) {
        return ProductCategory.builder()
                .name("category" + i)
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
}
