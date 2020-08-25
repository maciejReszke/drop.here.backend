package com.drop.here.backend.drophere.product.dto.response;

import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductResponse {

    @ApiModelProperty(value = "Product id", example = "1L")
    Long id;

    @ApiModelProperty(value = "Product name", example = "Kebap")
    String name;

    @ApiModelProperty(value = "Category", example = "Food")
    String category;

    @ApiModelProperty(value = "Unit", example = "piece")
    String unit;

    @ApiModelProperty(value = "Availability status", example = "AVAILABLE")
    ProductAvailabilityStatus availabilityStatus;

    @ApiModelProperty(value = "Product price", example = "50.33")
    BigDecimal price;

    @ApiModelProperty(value = "Product description", example = "Matka zona policjanta przynosi do hotelu blanta")
    String description;

    @ApiModelProperty(value = "Is deletable", example = "true")
    Boolean deletable;
}
