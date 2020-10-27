package com.drop.here.backend.drophere.shipment.repository;

import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentProductCustomizationRepository extends JpaRepository<ShipmentProductCustomization, Long> {
}
