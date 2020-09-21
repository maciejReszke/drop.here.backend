package com.drop.here.backend.drophere.spot.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpotCompanyResponse {

    @ApiModelProperty(value = "Spot id", example = "1")
    Long id;

    @ApiModelProperty(value = "Spot name", example = "Ryneczek lidla")
    String name;

    @ApiModelProperty(value = "Spot description", example = "Nie ma opisu bo brak dlugopis")
    String description;

    @ApiModelProperty(value = "Spot uid - required to join via hidden link", example = "name123xz")
    String uid;

    @ApiModelProperty(value = "Spot hidden status - describes if can be seen on map", example = "true")
    boolean hidden;

    @ApiModelProperty(value = "Is password needed to join given drop", example = "true")
    boolean requiresPassword;

    @ApiModelProperty(value = "Password needed to join spot (if is needed)", example = "Aezakmi")
    String password;

    @ApiModelProperty(value = "Does company owner must accept user to join region", example = "true")
    boolean requiresAccept;

    @ApiModelProperty(value = "X geo location (-180, 180)", example = "130.44213")
    Double xCoordinate;

    @ApiModelProperty(value = "Y geo location (-90, 90)", example = "-45.32132")
    Double yCoordinate;

    @ApiModelProperty(value = "Estimated radius in meters", example = "200")
    Integer estimatedRadiusMeters;

    @ApiModelProperty(value = "Spot was created at", example = "2020-08-08T12:44:55")
    String createdAt;

    @ApiModelProperty(value = "Spot was created at", example = "2020-08-08T12:44:55")
    String lastUpdatedAt;
}
