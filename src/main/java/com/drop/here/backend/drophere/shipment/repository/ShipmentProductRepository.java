package com.drop.here.backend.drophere.shipment.repository;

import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentProductRepository extends JpaRepository<ShipmentProduct, Long> {

    @Query("select sp from ShipmentProduct sp " +
            "join fetch sp.product where " +
            "sp.id in (:productsIds)")
    List<ShipmentProduct> findByIdWithProduct(List<Long> productsIds);
}
