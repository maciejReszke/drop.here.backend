package com.drop.here.backend.drophere.shipment.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ShipmentProductCalculation {
    BigDecimal unitPrice;

    BigDecimal unitCustomizationsPrice;

    BigDecimal unitSummarizedPrice;

    BigDecimal summarizedPrice;
}
