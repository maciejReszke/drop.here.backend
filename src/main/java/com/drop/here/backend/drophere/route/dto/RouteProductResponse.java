package com.drop.here.backend.drophere.route.dto;

import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class RouteProductResponse {

    @ApiModelProperty(value = "Is product limited", example = "true")
    boolean limitedAmount;

    @ApiModelProperty(value = "Given product amount for route (if limited amount = true) (must be dividable by unit fraction)", example = "15")
    Integer amount;

    @ApiModelProperty(value = "Base product price for every drop in this route", example = "15.55")
    BigDecimal price;

    @ApiModelProperty(value = "Product response")
    ProductResponse productResponse;
}
