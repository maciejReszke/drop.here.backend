package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ShipmentProductManagementService {

    // TODO: 18/10/2020 - gdy sie nie uda to odpowiedni wyjatek z innym statusem, nazwa?
    public void handle(Shipment shipment, ShipmentStatus newStatus) {

    }
}
