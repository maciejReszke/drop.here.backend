package com.drop.here.backend.drophere.route.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteDropRequest {
    @ApiModelProperty(value = "Drop name", example = "Drop the base", required = true)
    @NotBlank
    @Length(max = 50)
    private String name;

    @ApiModelProperty(value = "Drop description", example = "Description of dropping the base")
    @Length(max = 255)
    private String description;

    @NotNull
    @ApiModelProperty(value = "Spot id", example = "5", required = true)
    private Long spotId;

    @NotBlank
    @ApiModelProperty(value = "Drop start at", example = "17:15", required = true)
    private String startTime;

    @NotBlank
    @ApiModelProperty(value = "Drop end at", example = "17:30", required = true)
    private String endTime;
}
