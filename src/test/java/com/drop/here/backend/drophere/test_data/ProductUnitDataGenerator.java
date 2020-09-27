package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.product.entity.ProductUnit;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ProductUnitDataGenerator {
    public ProductUnit productUnit(int i) {
        return ProductUnit.builder()
                .name("productUnit" + i)
                .createdAt(LocalDateTime.now())
                .fractionable(false)
                .build();
    }
}
