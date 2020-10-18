package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentCompanyDecision;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class CompanyDecisionShipmentProcessingServiceFactory implements ShipmentProcessingService {
    private final AcceptCompanyDecisionShipmentProcessingService acceptCompanyDecisionShipmentProcessingService;
    private final RejectCompanyDecisionShipmentProcessingService rejectCompanyDecisionShipmentProcessingService;
    private final CancelCompanyDecisionShipmentProcessingService cancelCompanyDecisionShipmentProcessingService;

    // TODO: 18/10/2020 ??
    @Override
    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest request) {
        return API.Match(request.getShipmentCompanyDecisionRequest().getCompanyDecision()).of(
                Case($(ShipmentCompanyDecision.REJECT), () -> rejectCompanyDecisionShipmentProcessingService.process(shipment, request)),
                Case($(ShipmentCompanyDecision.ACCEPT), () -> acceptCompanyDecisionShipmentProcessingService.process(shipment, request)),
                Case($(ShipmentCompanyDecision.CANCEL), () -> cancelCompanyDecisionShipmentProcessingService.process(shipment, request))
        );
    }
}
