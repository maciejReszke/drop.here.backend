package com.drop.here.backend.drophere.product.dto.response;

import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductCustomizationWrapperResponse {

    @ApiModelProperty(value = "Customization id", example = "Roll")
    Long id;

    @ApiModelProperty(value = "Customization heading", example = "Roll")
    String heading;

    @ApiModelProperty(value = "Customization type", example = "SINGLE")
    ProductCustomizationWrapperType type;

    @ApiModelProperty(value = "Customization list")
    List<ProductCustomizationResponse> customizations;
}
