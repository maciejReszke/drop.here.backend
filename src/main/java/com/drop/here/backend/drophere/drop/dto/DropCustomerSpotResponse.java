package com.drop.here.backend.drophere.drop.dto;

import com.drop.here.backend.drophere.drop.enums.DropStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DropCustomerSpotResponse {

    @ApiModelProperty(value = "Drop uid", example = "dropuid123")
    String uid;

    @ApiModelProperty(value = "Drop name", example = "Drop the base")
    String name;

    @ApiModelProperty(value = "Drop description", example = "Description of drop")
    String description;

    @ApiModelProperty(value = "Drop start time", example = "2020-04-06T11:03:32")
    String startTime;

    @ApiModelProperty(value = "Drop end time", example = "2020-04-06T11:03:32")
    String endTime;

    @ApiModelProperty(value = "Drop status", example = "PREPARED")
    DropStatus status;

    @ApiModelProperty(value = "Are shipments automatically accepted", example = "true")
    boolean acceptShipmentsAutomatically;
}
