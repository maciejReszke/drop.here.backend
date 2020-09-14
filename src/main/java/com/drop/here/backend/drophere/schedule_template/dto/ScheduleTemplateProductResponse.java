package com.drop.here.backend.drophere.schedule_template.dto;

import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ScheduleTemplateProductResponse {

    @ApiModelProperty(value = "Is product limited", example = "true")
    boolean limitedAmount;

    @ApiModelProperty(value = "Given product amount for schedule (if limited amount = true)", example = "15")
    Integer amount;

    @ApiModelProperty(value = "Base product price", example = "15.55")
    BigDecimal price;

    @ApiModelProperty(value = "Product response")
    ProductResponse productResponse;
}
