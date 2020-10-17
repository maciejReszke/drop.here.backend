package com.drop.here.backend.drophere.shipment.repository;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByIdAndCustomer(Long shipmentId, Customer customer);
}
