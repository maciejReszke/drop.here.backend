package com.drop.here.backend.drophere.company.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CompanyCustomerRelationshipManagementRequest {

    @NotNull
    @ApiModelProperty(value = "To block customer", example = "true", required = true)
    private boolean block;

}
