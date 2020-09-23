package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.response.ProductCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductService productService;

    public Flux<ProductCategoryResponse> findAll(String companyUid) {
        return productService.findCategories(companyUid).stream()
                .map(ProductCategoryResponse::from)
                .collect(Collectors.toList());
    }
}
