package com.drop.here.backend.drophere.shipment.dto;


import com.drop.here.backend.drophere.shipment.enums.ShipmentCompanyDecision;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentCompanyDecisionRequest {

    @NotNull
    @ApiModelProperty(value = "Company decision", example = "REJECT", required = true)
    private ShipmentCompanyDecision companyDecision;

    @Length(max = 2048)
    @ApiModelProperty(value = "Company comment", example = "Dziendobry, respektujemy pana uczulenie na cebule")
    private String comment;
}
