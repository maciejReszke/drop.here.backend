package com.drop.here.backend.drophere.product.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@Value
public class ProductCategoryResponse {

    @ApiModelProperty(value = "Category name", example = "Junk food")
    String name;

    public static ProductCategoryResponse from(String category) {
        return new ProductCategoryResponse(category);
    }
}
