package com.drop.here.backend.drophere.shipment.repository;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByIdAndCustomer(Long shipmentId, Customer customer);


    @Query("select s from Shipment s where " +
            "s.customer = :customer and " +
            "(:shipmentStatus is null or s.status = :shipmentStatus) ")
    Page<Shipment> findByCustomerAndStatus(Customer customer, ShipmentStatus shipmentStatus, Pageable pageable);
}
