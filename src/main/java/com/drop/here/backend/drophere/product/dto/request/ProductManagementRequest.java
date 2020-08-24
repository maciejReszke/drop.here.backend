package com.drop.here.backend.drophere.product.dto.request;

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
    private String name;

    @NotBlank
    @Length(max = 255)
    private String category;

    @NotBlank
    private String unit;

    @PositiveOrZero
    private BigDecimal unitValue;

    @NotBlank
    private String availabilityStatus;

    @NotNull
    @Positive
    private BigDecimal averagePrice;

    @Length(max = 2048)
    private String description;
}
