package com.drop.here.backend.drophere.shipment.dto;

import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ShipmentProductCustomizationResponse {

    @ApiModelProperty(value = "Customization id", example = "5")
    Long id;

    @ApiModelProperty(value = "Customization wrapper id", example = "5")
    Long wrapperId;

    @ApiModelProperty(value = "Customization wrapper heading", example = "Roll")
    String wrapperHeading;

    @ApiModelProperty(value = "Customization wrapper type", example = "SINGLE")
    ProductCustomizationWrapperType wrapperType;

    @ApiModelProperty(value = "Customization price", example = "55.33")
    BigDecimal customizationPrice;

    @ApiModelProperty(value = "Customization value", example = "Bulke czostkowa")
    String customizationValue;


}
