package com.drop.here.backend.drophere.company.dto.response;

import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CompanyCustomerSpotMembershipResponse {
    @ApiModelProperty(value = "Spot membership status", example = "ACTIVE")
    SpotMembershipStatus membershipStatus;

    @ApiModelProperty(value = "Spot name", example = "Morenka")
    String spotName;

    @ApiModelProperty(value = "Spot id", example = "5")
    Long spotId;

    @ApiModelProperty(value = "Spot membership id", example = "6")
    Long spotMembershipId;

    @ApiModelProperty(value = "Spot uid", example = "spotUid1")
    String spotUid;
}
