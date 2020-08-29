package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationPrivilegesService;
import com.drop.here.backend.drophere.product.dto.response.ProductCustomizationResponse;
import com.drop.here.backend.drophere.product.dto.response.ProductCustomizationWrapperResponse;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchingService {
    private final ProductRepository productRepository;
    private final AuthenticationPrivilegesService authenticationPrivilegesService;
    private final ProductCustomizationService productCustomizationService;

    // TODO: 24/08/2020 company musi byc juz dostepne!!!
    public Page<ProductResponse> findAll(Pageable pageable,
                                         String companyUid,
                                         String[] desiredCategories,
                                         AccountAuthentication accountAuthentication) {
        final boolean isOwnCompanyOperation = authenticationPrivilegesService.isOwnCompanyOperation(accountAuthentication, companyUid);
        final ProductAvailabilityStatus[] desiredAvailabilityStatuses = getDesiredAvailabilityStatuses(isOwnCompanyOperation);
        final Page<Product> products = productRepository.findAll(companyUid, ArrayUtils.isEmpty(desiredCategories) ? null : desiredCategories, desiredAvailabilityStatuses, pageable);
        return toResponse(products, isOwnCompanyOperation);
    }

    private Page<ProductResponse> toResponse(Page<Product> products, boolean isOwnCompanyOperation) {
        final List<Long> productsIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        final List<ProductCustomizationWrapper> customizations = productCustomizationService.findCustomizations(productsIds);

        return products
                .map(product -> toProductResponse(product, isOwnCompanyOperation, findCustomizationWrappersForProduct(product, customizations)));
    }

    private List<ProductCustomizationWrapper> findCustomizationWrappersForProduct(Product product, List<ProductCustomizationWrapper> customizations) {
        return customizations.stream()
                .filter(customization -> customization.getProduct().getId().equals(product.getId()))
                .sorted(Comparator.comparing(ProductCustomizationWrapper::getId))
                .collect(Collectors.toList());
    }

    private ProductResponse toProductResponse(Product product, boolean isOwnCompanyOperation, List<ProductCustomizationWrapper> customizationWrappers) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategoryName())
                .unit(product.getUnitName())
                .availabilityStatus(product.getAvailabilityStatus())
                .price(product.getPrice())
                .description(product.getDescription())
                .deletable(isOwnCompanyOperation ? product.isDeletable() : null)
                .customizationsWrappers(toCustomizationWrappers(customizationWrappers))
                .build();
    }

    private List<ProductCustomizationWrapperResponse> toCustomizationWrappers(List<ProductCustomizationWrapper> customizationWrappers) {
        return customizationWrappers.stream()
                .map(this::toCustomizationWrapper)
                .collect(Collectors.toList());
    }

    private ProductCustomizationWrapperResponse toCustomizationWrapper(ProductCustomizationWrapper wrapper) {
        return ProductCustomizationWrapperResponse.builder()
                .type(wrapper.getType())
                .id(wrapper.getId())
                .heading(wrapper.getHeading())
                .customizations(toCustomizations(wrapper.getCustomizations()))
                .build();
    }

    private List<ProductCustomizationResponse> toCustomizations(Set<ProductCustomization> customizations) {
        return customizations.stream()
                .sorted(Comparator.comparing(ProductCustomization::getOrderNum))
                .map(this::toCustomization)
                .collect(Collectors.toList());
    }

    private ProductCustomizationResponse toCustomization(ProductCustomization customization) {
        return ProductCustomizationResponse.builder()
                .price(customization.getPrice())
                .value(customization.getValue())
                .build();
    }

    private ProductAvailabilityStatus[] getDesiredAvailabilityStatuses(boolean isOwnCompanyOperation) {
        return isOwnCompanyOperation
                ? ProductAvailabilityStatus.values()
                : new ProductAvailabilityStatus[]{ProductAvailabilityStatus.AVAILABLE};
    }
}
