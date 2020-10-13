package com.drop.here.backend.drophere.order.dto;

import com.drop.here.backend.drophere.order.enums.OrderCustomerDecision;
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
public class OrderCustomerDecisionRequest {

    @NotNull
    @ApiModelProperty(value = "Customer decisions", example = "CANCEL", required = true)
    private OrderCustomerDecision customerDecision;
}
