package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    private final ProductUnitRepository productUnitRepository;

    public ProductUnit getByName(String name) {
        return productUnitRepository.findByName(name)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Product unit with name %s was not found", name),
                        RestExceptionStatusCode.PRODUCT_UNIT_NOT_FOUND_BY_NAME));

    }
}
