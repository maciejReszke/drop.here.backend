package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class ProductMappingService {
    private final ProductUnitService productUnitService;

    public Product toEntity(ProductManagementRequest request, AccountAuthentication accountAuthentication) {
        final Product product = Product.builder()
                .createdAt(LocalDateTime.now())
                .company(accountAuthentication.getCompany())
                .build();
        update(product, request);
        return product;
    }

    public void update(Product product, ProductManagementRequest request) {
        final ProductUnit unit = productUnitService.getByName(request.getUnit());
        final LocalDateTime now = LocalDateTime.now();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setUnit(unit);
        product.setUnitName(unit.getName());
        product.setUnitFraction(request.getUnitFraction() == null ? BigDecimal.ONE : request.getUnitFraction().setScale(2, RoundingMode.DOWN));
        product.setAvailabilityStatus(ProductAvailabilityStatus.valueOf(request.getAvailabilityStatus()));
        product.setPrice(request.getPrice().setScale(2, RoundingMode.DOWN));
        product.setDescription(request.getDescription());
        product.setLastUpdatedAt(now);
    }
}
