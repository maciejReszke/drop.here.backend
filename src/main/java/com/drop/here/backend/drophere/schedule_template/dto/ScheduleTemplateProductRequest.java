package com.drop.here.backend.drophere.schedule_template.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTemplateProductRequest {

    @ApiModelProperty(value = "Given product amount for schedule (if limited amount = true)", example = "15")
    @PositiveOrZero
    private Integer amount;

    @ApiModelProperty(value = "Is product limited", example = "true", required = true)
    @NotNull
    private boolean limitedAmount;

    @NotNull
    @ApiModelProperty(value = "Base product price", example = "15.55", required = true)
    @Positive
    private BigDecimal price;

    @NotNull
    @ApiModelProperty(value = "Product id", example = "5", required = true)
    private Long productId;
}
