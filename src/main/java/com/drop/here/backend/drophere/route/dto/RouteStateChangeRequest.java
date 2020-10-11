package com.drop.here.backend.drophere.route.dto;

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
public class RouteStateChangeRequest {

    @ApiModelProperty(value = "Seller uid, null means do not changed profile uid", example = "goobarich123")
    private String changedProfileUid;

    @NotNull
    @ApiModelProperty(value = "New route status", example = "PREPARED", required = true)
    private RouteStatusChange newStatus;
}
