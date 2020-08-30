package com.drop.here.backend.drophere.product.dto.response;

import com.drop.here.backend.drophere.product.entity.ProductUnit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@Value
public class ProductUnitResponse {
    @ApiModelProperty(value = "Unit name", example = "kg")
    String name;

    public static ProductUnitResponse from(ProductUnit productUnit) {
        return new ProductUnitResponse(productUnit.getName());
    }
}
