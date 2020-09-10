package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.product.entity.ProductCategory;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ProductCategoryDataGenerator {
    public ProductCategory productCategory(int i) {
        return ProductCategory.builder()
                .name("productCategory" + i)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
