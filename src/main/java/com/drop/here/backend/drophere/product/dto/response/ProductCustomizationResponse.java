package com.drop.here.backend.drophere.product.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductCustomizationResponse {

    BigDecimal price;

    String value;
}
