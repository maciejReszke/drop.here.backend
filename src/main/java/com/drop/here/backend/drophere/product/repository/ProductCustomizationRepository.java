package com.drop.here.backend.drophere.product.repository;

import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCustomizationRepository extends JpaRepository<ProductCustomization, Long> {
    @Query("select pc from ProductCustomization pc " +
            "join fetch pc.wrapper where " +
            "pc.id in (:customizationsIds)")
    List<ProductCustomization> findCustomizationsWithWrapper(List<Long> customizationsIds);
}
