package com.drop.here.backend.drophere.route.entity;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// TODO: 25/09/2020
@Entity
public class Drop {

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
