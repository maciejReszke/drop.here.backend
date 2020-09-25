package com.drop.here.backend.drophere.product.repository;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCustomizationWrapperRepository extends JpaRepository<ProductCustomizationWrapper, Long> {
    Optional<ProductCustomizationWrapper> findByProduct(Product product);

    @Query("select pcw from ProductCustomizationWrapper pcw " +
            "left join fetch pcw.customizations where " +
            "pcw.product.id in :productsIds")
    List<ProductCustomizationWrapper> findByProductsIdsWithCustomizations(List<Long> productsIds);
}
