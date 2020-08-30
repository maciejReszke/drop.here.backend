package com.drop.here.backend.drophere.product.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCustomizationRequest {

    @ApiModelProperty(value = "Customization price", example = "2.40", required = true)
    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @ApiModelProperty(value = "Customization value", example = "Ciabatta", required = true)
    @NotBlank
    @Length(max = 255)
    private String value;
}
