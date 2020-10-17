package com.drop.here.backend.drophere.shipment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentCustomizationRequest {

    @ApiModelProperty(value = "Customization id", example = "55", required = true)
    @NotNull
    private Long id;
}
