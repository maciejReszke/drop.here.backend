package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

@Service
public class ProductCustomizationValidationService {

    public void validate(ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        Try.ofSupplier(() -> ProductCustomizationWrapperType.valueOf(productCustomizationWrapperRequest.getType()))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(
                        String.format("During validating product customization found invalid customization type %s", productCustomizationWrapperRequest.getCustomizations()),
                        RestExceptionStatusCode.PRODUCT_CUSTOMIZATION_INVALID_WRAPPER_TYPE
                ));
    }
}
