package com.drop.here.backend.drophere.product.dto.response;

import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

// TODO: 24/08/2020 swagger
@Value
@Builder
public class ProductResponse {

    Long id;

    String name;

    String category;

    String unit;

    ProductAvailabilityStatus availabilityStatus;

    BigDecimal averagePrice;

    String description;

    Boolean deletable;
}
