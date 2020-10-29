package com.drop.here.backend.drophere.drop.dto;

import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.route.dto.RouteProductRouteResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotBaseCustomerResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DropDetailedCustomerResponse {

    @ApiModelProperty(value = "Drop uid", example = "dropuid123")
    String uid;

    @ApiModelProperty(value = "Drop name", example = "Drop the base")
    String name;

    @ApiModelProperty(value = "Drop description", example = "Description of drop")
    String description;

    @ApiModelProperty(value = "Drop start time", example = "2020-04-06T11:03:32")
    String startTime;

    @ApiModelProperty(value = "Drop end time", example = "2020-04-06T11:03:32")
    String endTime;

    @ApiModelProperty(value = "Drop status", example = "PREPARED")
    DropStatus status;

    @ApiModelProperty(value = "Spot response")
    SpotBaseCustomerResponse spot;

    @ApiModelProperty(value = "Route products")
    List<RouteProductRouteResponse> products;

    @ApiModelProperty(value = "Seller profile uid or null", example = "15gang")
    String profileUid;

    @ApiModelProperty(value = "Seller profile first name or null", example = "Edmund")
    String profileFirstName;

    @ApiModelProperty(value = "Seller profile last name or null", example = "Panifoster")
    String profileLastName;

    @ApiModelProperty(value = "Should seller be streaming position", example = "true")
    boolean streamingPosition;

    @ApiModelProperty(value = "Are shipments automatically accepted", example = "true")
    boolean acceptShipmentsAutomatically;
}
