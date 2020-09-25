package com.drop.here.backend.drophere.route.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteRequest {

    @ApiModelProperty(value = "Route name", example = "Route 66", required = true)
    @NotBlank
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "Route description", example = "Description of route 66")
    @Length(max = 255)
    private String description;

    @ApiModelProperty(value = "Seller uid", example = "goobarich123")
    private String profileUid;

    @ApiModelProperty(value = "Drops")
    @Valid
    private List<@Valid RouteDropRequest> drops;

    @ApiModelProperty(value = "Products")
    @Valid
    private List<@Valid RouteProductRequest> products;

    @NotBlank
    @ApiModelProperty(value = "Route date", example = "2020-04-04", required = true)
    private String date;
}
