package com.drop.here.backend.drophere.product.repository;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCustomizationWrapperRepository extends JpaRepository<ProductCustomizationWrapper, Long> {
    Optional<ProductCustomizationWrapper> findByIdAndProduct(Long customizationId, Product product);
}
