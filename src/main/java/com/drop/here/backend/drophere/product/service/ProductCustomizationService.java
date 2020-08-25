package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCustomizationService {
    private final ProductCustomizationValidationService validationService;
    private final ProductCustomizationMappingService mappingService;
    private final ProductCustomizationWrapperRepository customizationWrapperRepository;

    @Transactional
    public ProductCustomizationWrapper createCustomizations(Product product, ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        validationService.validate(productCustomizationWrapperRequest);
        final ProductCustomizationWrapper customizationWrapper = mappingService.toCustomizationWrapper(product, productCustomizationWrapperRequest);
        log.info("Saving new customization wrapper for product with id {} company {}", product.getId(), product.getCompany().getUid());
        return customizationWrapperRepository.save(customizationWrapper);
    }

    public void deleteCustomization(Product product, Long customizationId) {
        final ProductCustomizationWrapper customizationWrapper = getCustomizationWrapper(product, customizationId);
        log.info("Deleting customization wrapper {} for product with id {} company {}", customizationWrapper.getId(), product.getId(), product.getCompany().getUid());
        customizationWrapperRepository.delete(customizationWrapper);
    }

    @Transactional(propagation = Propagation.NEVER)
    public ProductCustomizationWrapper updateCustomization(Product product, Long customizationId, ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        final ProductCustomizationWrapper wrapper = getCustomizationWrapper(product, customizationId);
        validationService.validate(productCustomizationWrapperRequest);
        final ProductCustomizationWrapper customizationWrapper = mappingService.toCustomizationWrapper(product, productCustomizationWrapperRequest);
        customizationWrapper.setId(wrapper.getId());
        log.info("Updating customization wrapper {} for product with id {} company {}", wrapper.getId(), product.getId(), product.getCompany().getUid());
        return customizationWrapperRepository.save(customizationWrapper);

    }

    private ProductCustomizationWrapper getCustomizationWrapper(Product product, Long customizationId) {
        return customizationWrapperRepository.findByIdAndProduct(customizationId, product)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Customization with id %s for product %s was not found", customizationId, product.getId()),
                        RestExceptionStatusCode.PRODUCT_CUSTOMIZATION_NOT_FOUND
                ));
    }
}
