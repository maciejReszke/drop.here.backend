package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.product.dto.response.ProductCategoryResponse;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private static final String SORT_BY_NAME_ATTRIBUTE = "name";

    public ProductCategory getByName(String name) {
        return productCategoryRepository.findByName(name)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Product category with name %s was not found", name),
                        RestExceptionStatusCode.PRODUCT_CATEGORY_NOT_FOUND_BY_NAME));

    }

    public List<ProductCategoryResponse> findAll() {
        return productCategoryRepository.findAll(Sort.by(SORT_BY_NAME_ATTRIBUTE)).stream()
                .map(ProductCategoryResponse::from)
                .collect(Collectors.toList());
    }
}
