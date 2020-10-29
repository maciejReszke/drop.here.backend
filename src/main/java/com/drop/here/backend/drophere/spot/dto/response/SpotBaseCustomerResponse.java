package com.drop.here.backend.drophere.spot.dto.response;

import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpotBaseCustomerResponse {
    @ApiModelProperty(value = "Spot name", example = "Ryneczek lidla")
    String name;

    @ApiModelProperty(value = "Spot description", example = "Nie ma opisu bo brak dlugopis")
    String description;

    @ApiModelProperty(value = "Spot uid - required to join via hidden link", example = "name123xz")
    String uid;

    @ApiModelProperty(value = "Is password needed to join given spot", example = "true")
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
    SpotMembershipStatus membershipStatus;

    @ApiModelProperty(value = "Company name", example = "Glodny maciek")
    String companyName;

    @ApiModelProperty(value = "Company uid", example = "uid123")
    String companyUid;

    @ApiModelProperty(value = "Does user want to receive notifications when company makes drop live", example = "true")
    boolean receiveLiveNotifications;

    @ApiModelProperty(value = "Does user want to receive notifications when company makes drop prepared", example = "true")
    boolean receivePreparedNotifications;

    @ApiModelProperty(value = "Does user want to receive notifications when company finishes drop", example = "true")
    boolean receiveFinishedNotifications;

    @ApiModelProperty(value = "Does user want to receive notifications when company delays drop", example = "true")
    boolean receiveDelayedNotifications;

    @ApiModelProperty(value = "Does user want to receive notifications when company cancels drop", example = "true")
    boolean receiveCancelledNotifications;

}
