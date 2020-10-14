package com.drop.here.backend.drophere.shipment.dto;

import com.drop.here.backend.drophere.shipment.enums.ShipmentCustomerDecision;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentCustomerDecisionRequest {

    @NotNull
    @ApiModelProperty(value = "Customer decision", example = "CANCEL", required = true)
    private ShipmentCustomerDecision customerDecision;
}
