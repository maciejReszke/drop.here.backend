package com.drop.here.backend.drophere.drop.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

// TODO: 04/10/2020
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropManagementRequest {

    @NotNull
    @ApiModelProperty(value = "New drop status", example = "DELAYED", required = true)
    private DropStatusChange newStatus;

    @ApiModelProperty(value = "Delay minutes amount", example = "15")
    @PositiveOrZero
    private Integer delayByMinutes;
}
