package com.drop.here.backend.drophere.order.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderProcessingRequest {
    OrderSubmissionRequest orderSubmissionRequest;
    OrderCustomerDecisionRequest orderCustomerDecisionRequest;

    public static OrderProcessingRequest submission(OrderSubmissionRequest submissionRequest) {
        return new OrderProcessingRequest(submissionRequest, null);
    }

    public static OrderProcessingRequest decision(OrderCustomerDecisionRequest decisionRequest) {
        return new OrderProcessingRequest(null, decisionRequest);
    }
}
