package com.drop.here.backend.drophere.product.dto.response;

import com.drop.here.backend.drophere.product.entity.ProductCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@Value
public class ProductCategoryResponse {

    @ApiModelProperty(value = "Category name", example = "Junk food")
    String name;

    public static ProductCategoryResponse from(ProductCategory productCategory) {
        return new ProductCategoryResponse(productCategory.getName());
    }
}
