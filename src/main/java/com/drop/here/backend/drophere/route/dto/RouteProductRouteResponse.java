package com.drop.here.backend.drophere.route.dto;

import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class RouteProductRouteResponse {

    @ApiModelProperty(value = "Route product id", example = "5")
    Long id;

    @ApiModelProperty(value = "Is product limited", example = "true")
    boolean limitedAmount;

    @ApiModelProperty(value = "Given product amount for route/drop (if limited amount = true) (must be dividable by unit fraction)", example = "15")
    BigDecimal amount;

    @ApiModelProperty(value = "Base product price for every drop in this route/drop", example = "15.55")
    BigDecimal price;

    @ApiModelProperty(value = "For route created product response")
    ProductResponse routeProductResponse;

    @ApiModelProperty(value = "Original product response")
    ProductResponse originalProductResponse;
}
