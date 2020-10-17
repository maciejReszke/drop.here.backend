package com.drop.here.backend.drophere.product.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductCustomizationResponse {

    // TODO: 17/10/2020
    @ApiModelProperty(value = "Customization choice id")
    Long id;

    @ApiModelProperty(value = "Customization choice price")
    BigDecimal price;

    @ApiModelProperty(value = "Customization choice value")
    String value;
}
