package com.drop.here.backend.drophere.shipment.service.processing_service.customer;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentCustomerDecision;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.ShipmentProcessingService;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;


@Service
@RequiredArgsConstructor
public class CustomerDecisionShipmentProcessingServiceFactory implements ShipmentProcessingService {
    private final CancelCustomerDecisionShipmentProcessingService cancelCustomerDecisionShipmentProcessingService;
    private final AcceptCustomerDecisionShipmentProcessingService acceptCustomerDecisionShipmentProcessingService;

    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest request) {
        return API.Match(request.getShipmentCustomerDecisionRequest().getCustomerDecision()).of(
                Case($(ShipmentCustomerDecision.ACCEPT), () -> acceptCustomerDecisionShipmentProcessingService.process(shipment, request)),
                Case($(ShipmentCustomerDecision.CANCEL), () -> cancelCustomerDecisionShipmentProcessingService.process(shipment, request))
        );
    }
}
