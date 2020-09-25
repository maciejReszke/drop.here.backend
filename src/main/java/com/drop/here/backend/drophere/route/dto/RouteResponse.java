package com.drop.here.backend.drophere.route.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class RouteResponse {

    @ApiModelProperty(value = "Route id", example = "5")
    Long id;

    @ApiModelProperty(value = "Route name", example = "Best seler")
    String name;

    @ApiModelProperty(value = "Amount of products in route", example = "15")
    int productsAmount;

    @ApiModelProperty(value = "Amount of drops in route", example = "15")
    int dropsAmount;

    @ApiModelProperty(value = "Seller profile uid", example = "15gang")
    String profileUid;

    @ApiModelProperty(value = "Route products")
    List<RouteProductResponse> products;

    @ApiModelProperty(value = "Route drops")
    List<RouteDropResponse> drops;

}
