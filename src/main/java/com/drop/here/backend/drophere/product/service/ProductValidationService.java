package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductValidationService {
    private final ProductUnitService productUnitService;

    public Mono<ProductManagementRequest> validateProductRequest(ProductManagementRequest productManagementRequest) {
        return validateUnit(productManagementRequest)
                .flatMap(this::validateStatus);
    }

    private Mono<ProductManagementRequest> validateStatus(ProductManagementRequest productManagementRequest) {
        return Try.ofSupplier(() -> ProductAvailabilityStatus.valueOf(productManagementRequest.getAvailabilityStatus()))
                .map(ignore -> Mono.just(productManagementRequest))
                .getOrElseGet(ignore -> Mono.error(() -> new RestIllegalRequestValueException(
                        String.format("Product with name %s has invalid availability status %s", productManagementRequest.getName(), productManagementRequest.getAvailabilityStatus()),
                        RestExceptionStatusCode.PRODUCT_INVALID_AVAILABILITY_STATUS
                )));
    }

    private Mono<ProductManagementRequest> validateUnit(ProductManagementRequest productManagementRequest) {
        return productUnitService.getByName(productManagementRequest.getUnit())
                .filter(unit -> productManagementRequest.getUnitFraction() != null && productManagementRequest.getUnitFraction().scale() > 0 && !unit.isFractionable())
                .flatMap(unit -> Mono.error(() -> new RestIllegalRequestValueException(
                        String.format("Product with name %s has fraction not integer %s but product unit %s can must be integer",
                                productManagementRequest.getName(), productManagementRequest.getUnitFraction(), unit.getName()),
                        RestExceptionStatusCode.PRODUCT_FRACTION_VALUE_NOT_INTEGER
                )))
                .map(ignore -> productManagementRequest)
                .switchIfEmpty(Mono.just(productManagementRequest));
    }
}
