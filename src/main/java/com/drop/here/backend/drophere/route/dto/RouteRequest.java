package com.drop.here.backend.drophere.route.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteRequest {

    @ApiModelProperty(value = "Unprepared route request model")
    @Valid
    private UnpreparedRouteRequest unpreparedRouteRequest;

    @ApiModelProperty(value = "Route state change request")
    @Valid
    private RouteStateChangeRequest routeStateChangeRequest;
}
