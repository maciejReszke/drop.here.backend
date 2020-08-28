package com.drop.here.backend.drophere.product.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductManagementRequest {

    @NotBlank
    @Length(max = 255)
    @ApiModelProperty(value = "Product name", example = "Hot dog", required = true)
    private String name;

    @NotBlank
    @Length(max = 255)
    @ApiModelProperty(value = "Product category", example = "FOOD", required = true)
    private String category;

    @NotBlank
    @ApiModelProperty(value = "Product unit", example = "kg", required = true)
    private String unit;

    @PositiveOrZero
    @ApiModelProperty(value = "Product unit value per order", example = "10", required = true)
    private BigDecimal unitValue;

    @NotBlank
    @ApiModelProperty(value = "Product availability status", example = "UNAVAILABLE", required = true)
    private String availabilityStatus;

    @NotNull
    @Positive
    @ApiModelProperty(value = "Product price", example = "33.45", required = true)
    private BigDecimal price;

    @Length(max = 512)
    @ApiModelProperty(value = "Product description", example = "Hot dog is a roll with a dog inside", required = true)
    private String description;
}
