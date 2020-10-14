package com.drop.here.backend.drophere.shipment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentCustomerSubmissionRequest {

    @ApiModelProperty(value = "Products", required = true)
    @NotBlank
    @NotNull
    @Valid
    private List<@Valid ShipmentProductRequest> products;
}
