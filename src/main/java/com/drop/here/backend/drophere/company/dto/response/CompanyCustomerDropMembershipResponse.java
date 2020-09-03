package com.drop.here.backend.drophere.company.dto.response;

import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class CompanyCustomerDropMembershipResponse {
    @ApiModelProperty(value = "Drop membership status", example = "ACTIVE")
    DropMembershipStatus membershipStatus;

    @NotBlank
    String dropName;

    @NotBlank
    Long dropId;

    @NotBlank
    String dropUid;
}
