package com.drop.here.backend.drophere.product.dto.response;

import com.drop.here.backend.drophere.drop.dto.DropProductResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

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

    @ApiModelProperty(value = "Minimal fraction", example = "0.1")
    BigDecimal unitFraction;

    @ApiModelProperty(value = "Product price per unit (1.0)", example = "50.33")
    BigDecimal price;

    @ApiModelProperty(value = "Product description", example = "Matka zona policjanta przynosi do hotelu blanta")
    String description;

    @ApiModelProperty(value = "Product customizations")
    List<ProductCustomizationWrapperResponse> customizationsWrappers;

    @ApiModelProperty(value = "Drops")
    List<DropProductResponse> drops;
}
