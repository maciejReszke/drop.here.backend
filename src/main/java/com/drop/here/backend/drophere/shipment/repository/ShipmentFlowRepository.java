package com.drop.here.backend.drophere.shipment.repository;

import com.drop.here.backend.drophere.shipment.entity.ShipmentFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentFlowRepository extends JpaRepository<ShipmentFlow, Long> {
    List<ShipmentFlow> findAllByShipmentIdIn(List<Long> shipmentsIds);
}
