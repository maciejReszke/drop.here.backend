package com.drop.here.backend.drophere.route.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteProductRequest {

    @ApiModelProperty(value = "Given product amount for route (if limited amount = true) (must be dividable by unit fraction)", example = "15")
    @PositiveOrZero
    private BigDecimal amount;

    @ApiModelProperty(value = "Is product limited", example = "true", required = true)
    @NotNull
    private boolean limitedAmount;

    @NotNull
    @ApiModelProperty(value = "Base product price for every drop in this route", example = "15.55", required = true)
    @Positive
    private BigDecimal price;

    @NotNull
    @ApiModelProperty(value = "Product id", example = "5", required = true)
    private Long productId;

}
