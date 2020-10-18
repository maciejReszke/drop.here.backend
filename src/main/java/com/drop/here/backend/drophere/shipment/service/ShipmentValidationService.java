package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import org.springframework.stereotype.Service;

@Service
public class ShipmentValidationService {

    // TODO: 17/10/2020 test, implement
    // TODO: 17/10/2020 sprawdzic unity - czy fractionable,
    // TODO: 17/10/2020 sprawdzic customizacje, ceny, czy wymagane i inne, co z limitami?
    // TODO: 18/10/2020 sprawdzic status dropa
    // TODO: 18/10/2020 problems/build problems intellij
    public void validateShipment(Shipment shipment) {

    }

    // TODO: 17/10/2020 test, impleent
    public void validateShipmentCustomerUpdate(Shipment shipment, ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest) {

    }

    // TODO: 18/10/2020 test, implemnet
    public void validateCancelCustomerDecision(Shipment shipment) {

    }

    // TODO: 18/10/2020 test, implement
    public void validateAcceptCustomerDecision(Shipment shipment) {

    }

    // TODO: 18/10/2020 test,implement
    public void validateCancelCompanyDecision(Shipment shipment) {

    }

    // TODO: 18/10/2020 test,implement
    public void validateAcceptCompanyDecision(Shipment shipment) {

    }
}
