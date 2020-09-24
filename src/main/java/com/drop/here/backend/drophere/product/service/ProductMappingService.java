package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductMappingService {
    private final ProductUnitService productUnitService;

    public Mono<Product> toEntity(ProductManagementRequest request, AccountAuthentication accountAuthentication) {
        final Product product = Product.builder()
                .createdAt(LocalDateTime.now())
                .company(accountAuthentication.getCompany())
                .build();
        return update(product, request);
    }

    public Mono<Product> update(Product product, ProductManagementRequest request) {
        return productUnitService.getByName(request.getUnit())
                .map(productUnit -> {
                    final LocalDateTime now = LocalDateTime.now();
                    product.setName(request.getName());
                    product.setCategory(request.getCategory());
                    product.setUnit(productUnit);
                    product.setUnitFraction(request.getUnitFraction() == null ? BigDecimal.ONE : request.getUnitFraction().setScale(2, RoundingMode.DOWN));
                    product.setAvailabilityStatus(ProductAvailabilityStatus.valueOf(request.getAvailabilityStatus()));
                    product.setPrice(request.getPrice().setScale(2, RoundingMode.DOWN));
                    product.setDescription(request.getDescription());
                    product.setLastUpdatedAt(now);
                    return product;
                });
    }
}
