package com.drop.here.backend.drophere.drop.dto.response;

import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DropCustomerResponse {
    @ApiModelProperty(value = "Drop name", example = "Ryneczek lidla")
    String dropName;

    @ApiModelProperty(value = "Drop description", example = "Nie ma opisu bo brak dlugopis")
    String dropDescription;

    @ApiModelProperty(value = "Drop uid - required to join via hidden link", example = "name123xz")
    String dropUid;

    @ApiModelProperty(value = "Is password needed to join given drop", example = "true")
    boolean requiresPassword;

    @ApiModelProperty(value = "Does company owner must accept user to join region", example = "true")
    boolean requiresAccept;

    @ApiModelProperty(value = "X geo location (-180, 180)", example = "130.44213")
    Double xCoordinate;

    @ApiModelProperty(value = "Y geo location (-90, 90)", example = "-45.32132")
    Double yCoordinate;

    @ApiModelProperty(value = "Estimated radius in meters", example = "200")
    Integer estimatedRadiusMeters;

    @ApiModelProperty(value = "Membership status, null means no membership", example = "ACTIVE", allowableValues = "ACTIVE, PENDING, null")
    DropMembershipStatus membershipStatus;

    @ApiModelProperty(value = "Company name", example = "Glodny maciek")
    String companyName;

    @ApiModelProperty(value = "Company uid", example = "uid123")
    String companyUid;

}
