package com.drop.here.backend.drophere.drop.dto.response;

import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DropCompanyMembershipResponse {

    @ApiModelProperty(value = "Drop membership status", example = "ACTIVE")
    DropMembershipStatus membershipStatus;

    @ApiModelProperty(value = "Customer first name", example = "Marian")
    String firstName;

    @ApiModelProperty(value = "Customer last name", example = "Paździoch")
    String lastName;

    @ApiModelProperty(value = "Customer id", example = "5")
    Long customerId;
}
