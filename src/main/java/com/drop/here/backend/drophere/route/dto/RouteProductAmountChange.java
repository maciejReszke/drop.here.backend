package com.drop.here.backend.drophere.route.dto;

import com.drop.here.backend.drophere.route.entity.RouteProduct;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RouteProductAmountChange {
    RouteProduct routeProduct;

    BigDecimal amountChange;
}
