package com.drop.here.backend.drophere.shipment.dto;

import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@Value
public class ShipmentFlowResponse {

    @ApiModelProperty(value = "Flow created at", example = "2020-04-06T11:03:32")
    String createdAt;

    @ApiModelProperty(value = "Shipment status", example = "PLACED")
    ShipmentStatus status;
}
