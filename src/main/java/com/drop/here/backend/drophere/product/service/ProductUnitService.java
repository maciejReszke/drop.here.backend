package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.product.dto.response.ProductUnitResponse;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    private final ProductUnitRepository productUnitRepository;
    private static final String SORT_BY_NAME_ATTRIBUTE = "name";

    // TODO: 14/09/2020 is can have fraction!
    public ProductUnit getByName(String name) {
        return productUnitRepository.findByName(name)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Product unit with name %s was not found", name),
                        RestExceptionStatusCode.PRODUCT_UNIT_NOT_FOUND_BY_NAME));

    }

    public List<ProductUnitResponse> findAll() {
        return productUnitRepository.findAll(Sort.by(SORT_BY_NAME_ATTRIBUTE)).stream()
                .map(ProductUnitResponse::from)
                .collect(Collectors.toList());
    }
}
