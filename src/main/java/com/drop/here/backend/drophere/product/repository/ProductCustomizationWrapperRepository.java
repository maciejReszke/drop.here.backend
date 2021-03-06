package com.drop.here.backend.drophere.product.repository;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCustomizationWrapperRepository extends JpaRepository<ProductCustomizationWrapper, Long> {
    @Query("select distinct pcw from ProductCustomizationWrapper pcw " +
            "left join fetch pcw.customizations " +
            "where pcw.product = :product")
    List<ProductCustomizationWrapper> findByProductWithCustomizations(Product product);

    @Query("select distinct pcw from ProductCustomizationWrapper pcw " +
            "left join fetch pcw.customizations where " +
            "pcw.product.id in :productsIds")
    List<ProductCustomizationWrapper> findByProductsIdsWithCustomizations(List<Long> productsIds);
}
