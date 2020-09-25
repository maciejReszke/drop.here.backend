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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCustomizationService {
    private final ProductCustomizationValidationService validationService;
    private final ProductCustomizationMappingService mappingService;
    private final ProductCustomizationWrapperRepository customizationWrapperRepository;

    @Transactional
    public void createCustomizations(Product product, ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        validationService.validate(productCustomizationWrapperRequest);
        final ProductCustomizationWrapper customizationWrapper = mappingService.toCustomizationWrapper(product, productCustomizationWrapperRequest);
        log.info("Saving new customization wrapper for product with id {}", product.getId());
        customizationWrapperRepository.save(customizationWrapper);
    }

    @Transactional
    public void deleteCustomization(Product product) {
        final ProductCustomizationWrapper customizationWrapper = getCustomizationWrapper(product);
        log.info("Deleting customization wrapper {} for product with id {}", customizationWrapper.getId(), product.getId());
        customizationWrapperRepository.delete(customizationWrapper);
    }

    @Transactional
    public void updateCustomization(Product product, ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        deleteCustomization(product);
        createCustomizations(product, productCustomizationWrapperRequest);
    }

    private ProductCustomizationWrapper getCustomizationWrapper(Product product) {
        return customizationWrapperRepository.findByProduct(product)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Customization for product %s was not found", product.getId()),
                        RestExceptionStatusCode.PRODUCT_CUSTOMIZATION_NOT_FOUND
                ));
    }

    public List<ProductCustomizationWrapper> findCustomizations(List<Long> productsIds) {
        return customizationWrapperRepository.findByProductsIdsWithCustomizations(productsIds);
    }
}
