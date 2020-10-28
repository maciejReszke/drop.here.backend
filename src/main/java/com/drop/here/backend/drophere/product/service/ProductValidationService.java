package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductCreationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductValidationService {
    private final ProductUnitService productUnitService;
    private final ProductCustomizationValidationService productCustomizationValidationService;

    // TODO: 27/10/2020 gdy jest fractionable to nie moze miec dodatkow!
    public void validateProductRequest(ProductManagementRequest productManagementRequest) {
        validateUnit(productManagementRequest);
        productCustomizationValidationService.validate(productManagementRequest.getProductCustomizationWrappers());
    }

    private void validateUnit(ProductManagementRequest productManagementRequest) {
        final ProductUnit productUnit = productUnitService.getByName(productManagementRequest.getUnit());
        if (productManagementRequest.getUnitFraction() != null && productManagementRequest.getUnitFraction().scale() > 0 && !productUnit.isFractionable()) {
            throw new RestIllegalRequestValueException(
                    String.format("Product with name %s has fraction not integer %s but product unit %s can must be integer",
                            productManagementRequest.getName(), productManagementRequest.getUnitFraction(), productUnit.getName()),
                    RestExceptionStatusCode.PRODUCT_FRACTION_VALUE_NOT_INTEGER
            );
        }
    }

    public void validateProductRequestUpdate(ProductManagementRequest productManagementRequest, Product product) {
        validateProductRequest(productManagementRequest);
        validateProductModification(product);
        productCustomizationValidationService.validate(productManagementRequest.getProductCustomizationWrappers());
    }

    public void validateProductModification(Product product) {
        if (product.getCreationType() != ProductCreationType.PRODUCT) {
            throw new RestIllegalRequestValueException(
                    String.format("Product with id %s cannot be changed because invalid creation type",
                            product.getId()),
                    RestExceptionStatusCode.PRODUCT_CHANGE_INVALID_CREATION_TYPE);
        }
    }
}
