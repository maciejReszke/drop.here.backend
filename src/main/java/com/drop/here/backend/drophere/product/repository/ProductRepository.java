package com.drop.here.backend.drophere.product.repository;

import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// TODO MONO:
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndCompanyUid(Long productId, String companyUid);

    @Query("select p from Product p where " +
            "p.company.uid = :companyUid and " +
            "((:desiredCategories) is null or lower(p.category) in (:desiredCategories)) and " +
            "(:name is null or lower(p.name) like :name) and " +
            "p.availabilityStatus in (:desiredStatuses)")
    Page<Product> findAll(String companyUid,
                          List<String> desiredCategories,
                          String name,
                          ProductAvailabilityStatus[] desiredStatuses,
                          Pageable pageable);

    @Query("select distinct p.category from Product p where " +
            "p.company.uid = :companyUid " +
            "order by p.category")
    List<String> findCategories(String companyUid);

    List<Product> findByIdIn(List<Long> productsIds);

    @Query("select p from Product p " +
            "join fetch p.image where " +
            "p.id = :productId and " +
            "p.company.uid = :companyUid")
    Optional<Product> findByIdAndCompanyUidWithImage(Long productId, String companyUid);
}
