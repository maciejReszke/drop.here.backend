package com.drop.here.backend.drophere.route.dto;

import com.drop.here.backend.drophere.route.enums.RouteStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteShortResponse {
    @ApiModelProperty(value = "Route id", example = "5")
    private Long id;

    @ApiModelProperty(value = "Route name", example = "Best seler")
    private String name;

    @ApiModelProperty(value = "Route status", example = "PREPARED")
    private RouteStatus status;

    @ApiModelProperty(value = "Amount of products in route", example = "15")
    private int productsAmount;

    @ApiModelProperty(value = "Amount of drops in route", example = "15")
    private int dropsAmount;

    @ApiModelProperty(value = "Seller profile uid", example = "15gang")
    private String profileUid;

    @ApiModelProperty(value = "Seller first name", example = "Endriu")
    private String profileFirstName;

    @ApiModelProperty(value = "Seller last name", example = "Golota")
    private String profileLastName;
}
