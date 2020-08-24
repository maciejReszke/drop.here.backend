package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategory getByName(String name) {
        return productCategoryRepository.findByName(name)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Product category with name %s was not found", name),
                        RestExceptionStatusCode.PRODUCT_CATEGORY_NOT_FOUND_BY_NAME));

    }
}
