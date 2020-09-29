package com.drop.here.backend.drophere.spot.dto.response;

import com.drop.here.backend.drophere.drop.dto.DropCustomerSpotResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class SpotDetailedCustomerResponse {

    @ApiModelProperty(value = "Upcoming drops (from this day 00:00 for next 7 days)")
    List<DropCustomerSpotResponse> drops;

    @ApiModelProperty(value = "Spot details")
    SpotBaseCustomerResponse spot;
}
