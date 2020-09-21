package com.drop.here.backend.drophere.spot.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotManagementRequest {

    @NotBlank
    @Length(max = 100)
    @ApiModelProperty(value = "Spot name", example = "Ryneczek lidla", required = true)
    private String name;

    @ApiModelProperty(value = "Spot description", example = "Nie ma opisu bo brak dlugopis")
    @Length(max = 512)
    private String description;

    @ApiModelProperty(value = "Spot hidden status - describes if can be seen on map", example = "true", required = true)
    @NotNull
    private boolean hidden;

    @ApiModelProperty(value = "Is password needed to join given drop", example = "true", required = true)
    @NotNull
    private boolean requiresPassword;

    @ApiModelProperty(value = "Password needed to join spot (if is needed)", example = "Aezakmi")
    @Length(max = 20)
    private String password;

    @ApiModelProperty(value = "Does company owner must accept user to join region", example = "true", required = true)
    @NotNull
    private boolean requiresAccept;

    @ApiModelProperty(value = "X geo location (-180, 180)", example = "130.44213", required = true)
    @Max(180)
    @Min(-180)
    @NotNull
    private Double xCoordinate;

    @ApiModelProperty(value = "Y geo location (-90, 90)", example = "-45.32132", required = true)
    @Max(90)
    @Min(-90)
    @NotNull
    private Double yCoordinate;

    @ApiModelProperty(value = "Estimated radius in meters", example = "200", required = true)
    @PositiveOrZero
    @Max(50000)
    @NotNull
    private Integer estimatedRadiusMeters;
}
