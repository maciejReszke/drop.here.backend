package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCustomizationService {
    private final ProductCustomizationWrapperRepository customizationWrapperRepository;

    public List<ProductCustomizationWrapper> findCustomizations(List<Long> productsIds) {
        return customizationWrapperRepository.findByProductsIdsWithCustomizations(productsIds);
    }

    // TODO: 26/09/2020 test + test w bazie jak to wyglada!!!
    public List<ProductCustomizationWrapper> createReadOnlyCopy(Product templateProduct) {
        return customizationWrapperRepository.findByProductWithCustomizations(templateProduct)
                .stream()
                .map(templateWrapper -> {
                    final ProductCustomizationWrapper customizationWrapper = templateWrapper.toBuilder()
                            .id(null)
                            .product(templateProduct)
                            .build();
                    customizationWrapper.setCustomizations(createReadOnlyCopies(templateWrapper.getCustomizations(), customizationWrapper));
                    return customizationWrapper;
                })
                .collect(Collectors.toList());
    }

    private Set<ProductCustomization> createReadOnlyCopies(Set<ProductCustomization> customizations, ProductCustomizationWrapper customizationWrapper) {
        return customizations.stream()
                .map(customization -> createReadOnlyCopy(customization, customizationWrapper))
                .collect(Collectors.toSet());
    }

    private ProductCustomization createReadOnlyCopy(ProductCustomization customization, ProductCustomizationWrapper customizationWrapper) {
        return customization.toBuilder()
                .id(null)
                .wrapper(customizationWrapper)
                .build();
    }
}
