package com.drop.here.backend.drophere.company.dto.response;

import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CompanyManagementResponse {

    @ApiModelProperty(value = "Is company registered", example = "true")
    boolean registered;

    @ApiModelProperty(value = "Company name", example = "Glodny Maciek")
    String name;

    @ApiModelProperty(value = "Company uid", example = "glodny-maciek")
    String uid;

    @ApiModelProperty(value = "Company country", example = "Poland")
    String country;

    @ApiModelProperty(value = "Visibility status", example = "VISIBLE")
    CompanyVisibilityStatus visibilityStatus;

    @ApiModelProperty(value = "Profiles count", example = "5")
    Long profilesCount;
}
