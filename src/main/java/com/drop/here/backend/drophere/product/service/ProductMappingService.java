package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductCreationType;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductMappingService {
    private final ProductUnitService productUnitService;
    private final ProductCustomizationMappingService productCustomizationMappingService;

    public Product toEntity(ProductManagementRequest request, AccountAuthentication accountAuthentication) {
        final Product product = Product.builder()
                .createdAt(LocalDateTime.now())
                .company(accountAuthentication.getCompany())
                .creationType(ProductCreationType.PRODUCT)
                .customizationWrappers(new ArrayList<>())
                .build();
        update(product, request);
        return product;
    }

    public void update(Product product, ProductManagementRequest request) {
        final ProductUnit unit = productUnitService.getByName(request.getUnit());
        final LocalDateTime now = LocalDateTime.now();
        product.getCustomizationWrappers().forEach(customizationWrapper -> customizationWrapper.setProduct(null));
        product.getCustomizationWrappers().clear();
        product.setName(request.getName());
        product.setCategory(request.getCategory().toUpperCase());
        product.setUnit(unit);
        product.setUnitName(unit.getName());
        product.setUnitFraction(request.getUnitFraction() == null ? BigDecimal.ONE : request.getUnitFraction().setScale(2, RoundingMode.DOWN));
        product.setPrice(request.getPrice().setScale(2, RoundingMode.DOWN));
        product.setDescription(request.getDescription());
        product.setLastUpdatedAt(now);
        buildCustomizationWrappers(request, product).forEach(customizationWrapper -> product.getCustomizationWrappers().add(customizationWrapper));
    }

    private List<ProductCustomizationWrapper> buildCustomizationWrappers(ProductManagementRequest request, Product product) {
        return request.getProductCustomizationWrappers().stream()
                .map(wrapperRequest -> productCustomizationMappingService.toCustomizationWrapper(product, wrapperRequest))
                .collect(Collectors.toList());
    }
}
