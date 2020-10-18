package com.drop.here.backend.drophere.shipment.service.processing_service;

import com.drop.here.backend.drophere.shipment.dto.ShipmentProcessingRequest;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.enums.ShipmentProcessOperation;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.ByCompanyUpdatedShipmentProcessingService;
import com.drop.here.backend.drophere.shipment.service.processing_service.company.CompanyDecisionShipmentProcessingServiceFactory;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.ByCustomerUpdatedShipmentProcessingService;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.CustomerDecisionShipmentProcessingServiceFactory;
import com.drop.here.backend.drophere.shipment.service.processing_service.customer.NewShipmentProcessingService;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@RequiredArgsConstructor
public class ShipmentProcessingServiceFactory {
    private final NewShipmentProcessingService newShipmentProcessingService;
    private final ByCustomerUpdatedShipmentProcessingService byCustomerUpdatedShipmentProcessingService;
    private final CustomerDecisionShipmentProcessingServiceFactory customerDecisionShipmentProcessingServiceFactory;
    private final CompanyDecisionShipmentProcessingServiceFactory companyDecisionShipmentProcessingServiceFactory;
    private final ByCompanyUpdatedShipmentProcessingService byCompanyUpdatedShipmentProcessingService;

    public ShipmentStatus process(Shipment shipment, ShipmentProcessingRequest request, ShipmentProcessOperation shipmentProcessOperation) {
        return API.Match(shipmentProcessOperation).of(
                Case($(ShipmentProcessOperation.NEW), () -> newShipmentProcessingService.process(shipment, request)),
                Case($(ShipmentProcessOperation.BY_CUSTOMER_UPDATED), () -> byCustomerUpdatedShipmentProcessingService.process(shipment, request)),
                Case($(ShipmentProcessOperation.CUSTOMER_DECISION), () -> customerDecisionShipmentProcessingServiceFactory.process(shipment, request)),
                Case($(ShipmentProcessOperation.BY_COMPANY_UPDATED), () -> byCompanyUpdatedShipmentProcessingService.process(shipment, request)),
                Case($(ShipmentProcessOperation.COMPANY_DECISION), () -> companyDecisionShipmentProcessingServiceFactory.process(shipment, request))
        );
    }
}
