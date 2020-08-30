package com.drop.here.backend.drophere.company.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CompanyManagementRequest {

    @ApiModelProperty(value = "Company name", example = "Glodny maciek", required = true)
    @Length(max = 50)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "Country name", example = "Poland", required = true)
    @NotBlank
    private String country;

    @ApiModelProperty(value = "Company visibility status", example = "VISIBLE", required = true)
    @NotBlank
    private String visibilityStatus;
}
