package com.drop.here.backend.drophere.shipment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentProductRequest {

    @NotNull
    @Positive
    @ApiModelProperty(value = "Route product id", example = "5", required = true)
    private Long routeProductId;

    @NotNull
    @Positive
    @ApiModelProperty(value = "Amount of products", example = "55", required = true)
    private BigDecimal amount;
}
