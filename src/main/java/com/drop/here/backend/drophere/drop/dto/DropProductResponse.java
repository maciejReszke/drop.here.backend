package com.drop.here.backend.drophere.drop.dto;

import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.route.dto.RouteProductProductResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DropProductResponse {
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

    @ApiModelProperty(value = "Spot X geo location (-180, 180)", example = "130.44213")
    Double spotXCoordinate;

    @ApiModelProperty(value = "Spot Y geo location (-90, 90)", example = "-45.32132")
    Double spotYCoordinate;

    @ApiModelProperty(value = "Spot estimated radius in meters", example = "200")
    Integer spotEstimatedRadiusMeters;

    @ApiModelProperty(value = "Spot name", example = "Ryneczek lidla")
    String spotName;

    @ApiModelProperty(value = "Spot description", example = "Nie ma opisu bo brak dlugopis")
    String spotDescription;

    @ApiModelProperty(value = "Spot uid - required to join via hidden link", example = "name123xz")
    String spotUid;

    @ApiModelProperty(value = "Route product")
    RouteProductProductResponse routeProduct;
}
