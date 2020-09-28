package com.drop.here.backend.drophere.company.dto.response;

import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CompanyCustomerResponse {

    @ApiModelProperty(value = "Customer id", example = "5")
    Long customerId;

    @ApiModelProperty(value = "Customer first name", example = "Marian")
    String firstName;

    @ApiModelProperty(value = "Customer last name", example = "Paździoch")
    String lastName;

    @ApiModelProperty(value = "Company customer relationship", example = "ACTIVE", allowableValues = "ACTIVE, BLOCKED")
    CompanyCustomerRelationshipStatus relationshipStatus;

    @ApiModelProperty(value = "Company customer memberships")
    List<CompanyCustomerSpotMembershipResponse> companyCustomerSpotMemberships;
}
