package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.schedule_template.service.ScheduleTemplateService;
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
        productUnitService.getByName(productManagementRequest.getUnit());
    }

    public void validateProductDelete(Product product) {
        if (!product.isDeletable()) {
            throw new RestIllegalRequestValueException(String.format(
                    "Product to be removed with id %s is not deletable", product.getId()),
                    RestExceptionStatusCode.PRODUCT_DELETE_NOT_DELETABLE);
        }
    }
}
