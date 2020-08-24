package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductMappingService {
    private final ProductCategoryService categoryService;
    private final ProductUnitService productUnitService;

    public Product toEntity(ProductManagementRequest request, AccountAuthentication accountAuthentication) {
        final LocalDateTime now = LocalDateTime.now();
        final Product product = Product.builder()
                .createdAt(now)
                .deletable(true)
                .company(accountAuthentication.getCompany())
                .build();
        return update(product, request);
    }

    public Product update(Product product, ProductManagementRequest request) {
        final ProductCategory category = categoryService.getByName(request.getCategory());
        final ProductUnit unit = productUnitService.getByName(request.getUnit());
        final LocalDateTime now = LocalDateTime.now();
        return product.toBuilder()
                .name(request.getName())
                .category(category)
                .categoryName(category.getName())
                .unit(unit)
                .unitName(unit.getName())
                .unitValue(request.getUnitValue() == null ? BigDecimal.ONE : request.getUnitValue().setScale(2, RoundingMode.DOWN))
                .availabilityStatus(ProductAvailabilityStatus.valueOf(request.getAvailabilityStatus()))
                .averagePrice(request.getAveragePrice().setScale(2, RoundingMode.DOWN))
                .description(request.getDescription())
                .lastUpdatedAt(now)
                .build();
    }
}
