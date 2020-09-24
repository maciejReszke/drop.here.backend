package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.product.dto.response.ProductUnitResponse;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    private final ProductUnitRepository productUnitRepository;
    private static final String SORT_BY_NAME_ATTRIBUTE = "name";

    public Mono<ProductUnit> getByName(String name) {
        return productUnitRepository.findByName(name)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Product unit with name %s was not found", name),
                        RestExceptionStatusCode.PRODUCT_UNIT_NOT_FOUND_BY_NAME)));
    }

    public Flux<ProductUnitResponse> findAll() {
        return productUnitRepository.findAll(Sort.by(SORT_BY_NAME_ATTRIBUTE))
                .map(ProductUnitResponse::from);
    }
}
