package com.drop.here.backend.drophere.customer.dto;

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
@Builder
public class CustomerManagementRequest {

    @NotBlank
    @Length(max = 50)
    @ApiModelProperty(value = "Customer first name", example = "Mioter", required = true)
    private String firstName;

    @NotBlank
    @Length(max = 50)
    @ApiModelProperty(value = "Customer last name", example = "Kamszota", required = true)
    private String lastName;
}
