package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO MONO:
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCustomizationService {
    private final ProductCustomizationValidationService validationService;
    private final ProductCustomizationMappingService mappingService;
    private final ProductCustomizationWrapperRepository customizationWrapperRepository;

    // todo bylo transactional
    // TODO: 25/09/2020
    public ProductCustomizationWrapper createCustomizations(Product product, ProductCustomizationWrapperRequest productCustomizationWrapperRequest, AccountAuthentication authentication) {
        /*validationService.validate(productCustomizationWrapperRequest);
        final ProductCustomizationWrapper customizationWrapper = mappingService.toCustomizationWrapper(product, productCustomizationWrapperRequest);
        log.info("Saving new customization wrapper for product with id {} company {}", product.getId(), authentication.getCompany().getUid());
        return customizationWrapperRepository.save(customizationWrapper);*/
        return null;
    }

    // TODO: 25/09/2020
    public void deleteCustomization(Product product, Long customizationId, AccountAuthentication authentication) {
        /*final ProductCustomizationWrapper customizationWrapper = getCustomizationWrapper(product, customizationId);
        log.info("Deleting customization wrapper {} for product with id {} company {}", customizationWrapper.getId(), product.getId(), authentication.getCompany().getUid());
        customizationWrapperRepository.delete(customizationWrapper);*/
    }

    // TODO: 25/09/2020
    // todo bylo transactional(propagation = Propagation.NEVER)/**/
    public ProductCustomizationWrapper updateCustomization(Product product, Long customizationId, ProductCustomizationWrapperRequest productCustomizationWrapperRequest, AccountAuthentication authentication) {
/*        final ProductCustomizationWrapper wrapper = getCustomizationWrapper(product, customizationId);
        validationService.validate(productCustomizationWrapperRequest);
        final ProductCustomizationWrapper customizationWrapper = mappingService.toCustomizationWrapper(product, productCustomizationWrapperRequest);
        customizationWrapper.setId(wrapper.getId());*//**//*
        customizationWrapper.setVersion(wrapper.getVersion());
        log.info("Updating customization wrapper {} for product with id {} company {}", wrapper.getId(), product.getId(), authentication.getCompany().getUid());
        return customizationWrapperRepository.save(customizationWrapper);*/

        return null;
    }

    private ProductCustomizationWrapper getCustomizationWrapper(Product product, Long customizationId) {
        return customizationWrapperRepository.findByIdAndProduct(customizationId, product)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Customization with id %s for product %s was not found", customizationId, product.getId()),
                        RestExceptionStatusCode.PRODUCT_CUSTOMIZATION_NOT_FOUND
                ));
    }

    public List<ProductCustomizationWrapper> findCustomizations(List<Long> productsIds) {
        return customizationWrapperRepository.findByProductsIdsWithCustomizations(productsIds);
    }
}
