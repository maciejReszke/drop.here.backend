package com.drop.here.backend.drophere.drop.dto.response;

import com.drop.here.backend.drophere.drop.enums.DropLocationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DropCompanyResponse {

    @ApiModelProperty(value = "Drop name", example = "Ryneczek lidla")
    String name;

    @ApiModelProperty(value = "Drop description", example = "Nie ma opisu bo brak dlugopis")
    String description;

    @ApiModelProperty(value = "Drop uid - required to join via hidden link", example = "name123xz")
    String uid;

    @ApiModelProperty(value = "Drop hidden status - describes if can be seen on map", example = "true")
    boolean hidden;

    @ApiModelProperty(value = "Is password needed to join given drop", example = "true")
    boolean requiresPassword;

    @ApiModelProperty(value = "Password needed to join drop (if is needed)", example = "Aezakmi")
    String password;

    @ApiModelProperty(value = "Location type", example = "HIDDEN")
    DropLocationType locationType;

    @ApiModelProperty(value = "Does company owner must accept user to join region", example = "true")
    boolean requiresAccept;

    @ApiModelProperty(value = "X geo location (-180, 180)", example = "130.44213")
    Double xCoordinate;

    @ApiModelProperty(value = "Y geo location (-90, 90)", example = "-45.32132")
    Double yCoordinate;

    @ApiModelProperty(value = "Estimated radius in meters", example = "200")
    Integer estimatedRadiusMeters;

    @ApiModelProperty(value = "Drop was created at", example = "2020-08-08T12:44:55")
    String createdAt;

    @ApiModelProperty(value = "Drop was created at", example = "2020-08-08T12:44:55")
    String lastUpdatedAt;
}
