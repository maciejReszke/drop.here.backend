package com.drop.here.backend.drophere.customer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerManagementResponse {

    @ApiModelProperty(value = "Is customer registered", example = "true")
    boolean registered;

    @ApiModelProperty(value = "Customer first name", example = "Mioter")
    String firstName;

    @ApiModelProperty(value = "Customer last name", example = "Kamszota")
    String lastName;

    @ApiModelProperty(value = "Customer id", example = "5L")
    Long id;
}
