package com.drop.here.backend.drophere.shipment.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShipmentProcessingRequest {
    ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest;
    ShipmentCustomerDecisionRequest shipmentCustomerDecisionRequest;
    ShipmentCompanyDecisionRequest shipmentCompanyDecisionRequest;

    public static ShipmentProcessingRequest customerSubmission(ShipmentCustomerSubmissionRequest submissionRequest) {
        return new ShipmentProcessingRequest(submissionRequest, null, null);
    }

    public static ShipmentProcessingRequest customerDecision(ShipmentCustomerDecisionRequest decisionRequest) {
        return new ShipmentProcessingRequest(null, decisionRequest, null);
    }

    public static ShipmentProcessingRequest companyDecision(ShipmentCompanyDecisionRequest decisionRequest) {
        return new ShipmentProcessingRequest(null, null, decisionRequest);
    }
}
