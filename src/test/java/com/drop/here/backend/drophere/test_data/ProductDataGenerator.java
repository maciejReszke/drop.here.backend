package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class ProductDataGenerator {
    public Product product(int i, ProductCategory category, ProductUnit unit, Company company) {
        return Product.builder()
                .name("productName" + i)
                .category(category)
                .categoryName(category.getName())
                .unit(unit)
                .unitName(unit.getName())
                .unitValue("unitValue" + i)
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE)
                .averagePrice(BigDecimal.valueOf(123 + i))
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
}
