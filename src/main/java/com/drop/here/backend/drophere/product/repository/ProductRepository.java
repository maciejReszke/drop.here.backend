package com.drop.here.backend.drophere.product.repository;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndCompanyUid(Long productId, String companyUid);

    @Query("select p from Product p where " +
            "p.company.uid = :companyUid and " +
            "(:desiredCategories is null or  p.categoryName in :desiredCategories) and " +
            "p.availabilityStatus in :desiredStatuses")
    Page<Product> findAll(String companyUid, String[] desiredCategories, ProductAvailabilityStatus[] desiredStatuses, Pageable pageable);
}
