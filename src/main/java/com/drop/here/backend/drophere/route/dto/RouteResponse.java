package com.drop.here.backend.drophere.route.dto;

import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
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

    @ApiModelProperty(value = "Route description", example = "Advanced description of route")
    String description;

    @ApiModelProperty(value = "Route status", example = "PREPARED")
    RouteStatus status;

    @ApiModelProperty(value = "Amount of products in route", example = "15")
    int productsAmount;

    @ApiModelProperty(value = "Amount of drops in route", example = "15")
    int dropsAmount;

    @ApiModelProperty(value = "Seller profile uid", example = "15gang")
    String profileUid;

    @ApiModelProperty(value = "Seller profile first name", example = "Edmund")
    String profileFirstName;

    @ApiModelProperty(value = "Seller profile last name", example = "Panifoster")
    String profileLastName;

    @ApiModelProperty(value = "Route products")
    List<RouteProductRouteResponse> products;

    @ApiModelProperty(value = "Route drops")
    List<DropRouteResponse> drops;

    @ApiModelProperty(value = "Route date", example = "2020-04-04")
    String routeDate;

}
