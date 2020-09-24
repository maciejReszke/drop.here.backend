package com.drop.here.backend.drophere.common.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@Value
public class ResourceOperationResponse {

    @ApiModelProperty(value = "Operation status", example = "CREATED")
    ResourceOperationStatus operationStatus;

    @ApiModelProperty(value = "Resource id", example = "Abcdef")
    String id;
}
