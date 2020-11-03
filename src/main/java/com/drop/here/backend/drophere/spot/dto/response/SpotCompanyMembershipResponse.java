package com.drop.here.backend.drophere.spot.dto.response;

import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpotCompanyMembershipResponse {

    @ApiModelProperty(value = "Spot membership status", example = "ACTIVE")
    SpotMembershipStatus membershipStatus;

    @ApiModelProperty(value = "Customer first name", example = "Marian")
    String firstName;

    @ApiModelProperty(value = "Customer last name", example = "Paździoch")
    String lastName;

    @ApiModelProperty(value = "Customer id", example = "5")
    Long customerId;

    @ApiModelProperty(value = "Spot membership id", example = "6")
    Long spotMembershipId;
}
