package com.drop.here.backend.drophere.company.dto.response;

import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class CompanyCustomerSpotMembershipResponse {
    @ApiModelProperty(value = "Spot membership status", example = "ACTIVE")
    SpotMembershipStatus membershipStatus;

    @NotBlank
    String spotName;

    @NotBlank
    Long spotId;

    @NotBlank
    String spotUid;
}
