package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.stream.Collectors;

@Service
public class ShipmentValidationService {

    // TODO: 17/10/2020 test, implement
    // TODO: 17/10/2020 sprawdzic unity - czy fractionable,
    // TODO: 17/10/2020 sprawdzic customizacje, ceny, czy wymagane i inne, co z limitami?
    // TODO: 18/10/2020 sprawdzic status dropa
    public void validateShipment(Shipment shipment) {

    }

    public void validateShipmentCustomerUpdate(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.PLACED),
                ShipmentStatus.PLACED
        );
    }

    public void validateCancelCustomerDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.ACCEPTED, ShipmentStatus.PLACED, ShipmentStatus.COMPROMISED),
                ShipmentStatus.CANCELLED
        );
    }

    public void validateAcceptCustomerDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.COMPROMISED),
                ShipmentStatus.ACCEPTED
        );
    }

    public void validateCancelCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.CANCEL_REQUESTED),
                ShipmentStatus.CANCELLED
        );
    }

    public void validateAcceptCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.PLACED, ShipmentStatus.DELIVERED),
                ShipmentStatus.ACCEPTED
        );
    }

    public void validateDeliverCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.ACCEPTED, ShipmentStatus.CANCEL_REQUESTED),
                ShipmentStatus.DELIVERED
        );
    }

    public void validateRejectCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.COMPROMISED, ShipmentStatus.PLACED, ShipmentStatus.ACCEPTED),
                ShipmentStatus.REJECTED
        );
    }

    private void validateUpdateCurrentStatus(Shipment shipment, EnumSet<ShipmentStatus> desiredStatuses, ShipmentStatus updateToStatus) {
        if (!desiredStatuses.contains(shipment.getStatus())) {
            throw new RestIllegalRequestValueException(String.format(
                    "In order to change shipment status to %s it must be in %s but was %s", updateToStatus, desiredStatuses
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(",")), shipment.getStatus()),
                    RestExceptionStatusCode.SHIPMENT_UPDATE_INVALID_STATUS);
        }
    }
}
