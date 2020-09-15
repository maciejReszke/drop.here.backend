package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductValidationService {
    private final ProductUnitService productUnitService;

    public void validateProductRequest(ProductManagementRequest productManagementRequest) {
        validateUnit(productManagementRequest);
        validateStatus(productManagementRequest);
    }

    private void validateStatus(ProductManagementRequest productManagementRequest) {
        Try.ofSupplier(() -> ProductAvailabilityStatus.valueOf(productManagementRequest.getAvailabilityStatus()))
                .getOrElseThrow(ignore -> new RestIllegalRequestValueException(
                        String.format("Product with name %s has invalid availability status %s", productManagementRequest.getName(), productManagementRequest.getAvailabilityStatus()),
                        RestExceptionStatusCode.PRODUCT_INVALID_AVAILABILITY_STATUS
                ));
    }

    private void validateUnit(ProductManagementRequest productManagementRequest) {
        final ProductUnit productUnit = productUnitService.getByName(productManagementRequest.getUnit());
        if (productManagementRequest.getUnitFraction().scale() > 0 && !productUnit.isFractionable()) {
            throw new RestIllegalRequestValueException(
                    String.format("Product with name %s has fraction not integer %s but product unit %s can must be integer",
                            productManagementRequest.getName(), productManagementRequest.getUnitFraction(), productUnit.getName()),
                    RestExceptionStatusCode.PRODUCT_FRACTION_VALUE_NOT_INTEGER
            );
        }
    }
}
