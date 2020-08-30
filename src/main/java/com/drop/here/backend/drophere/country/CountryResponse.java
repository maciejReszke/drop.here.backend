package com.drop.here.backend.drophere.country;

import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@Value
public class CountryResponse {

    @ApiModelProperty(value = "Name of country", example = "POLAND")
    String name;

    @ApiModelProperty(value = "Country mobile prefix", example = "+48")
    String mobilePrefix;

    public static CountryResponse from(Country country) {
        return new CountryResponse(country.getName(), country.getMobilePrefix());
    }
}
