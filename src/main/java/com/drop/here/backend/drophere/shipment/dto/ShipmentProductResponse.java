package com.drop.here.backend.drophere.shipment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class ShipmentProductResponse {

    @ApiModelProperty(value = "Shipment product id", example = "55")
    Long id;

    @ApiModelProperty(value = "Referred product id", example = "55.32")
    Long productId;

    @ApiModelProperty(value = "Referred product name", example = "Burger")
    String productName;

    @ApiModelProperty(value = "Referred product description", example = "Burger bardzo pyszny kizo kizo")
    String productDescription;

    @ApiModelProperty(value = "Chosen customization")
    List<ShipmentProductCustomizationResponse> customizations;

    @ApiModelProperty(value = "Product price per unit (w/o customizations)", example = "23.44")
    BigDecimal unitPrice;

    @ApiModelProperty(value = "Product customizations price (per unit)", example = "12.44")
    BigDecimal customizationsPrice;

    @ApiModelProperty(value = "Product summarized price (for all unit)", example = "123.33")
    BigDecimal summarizedPrice;

    @ApiModelProperty(value = "Product quantity", example = "2")
    BigDecimal quantity;
}
