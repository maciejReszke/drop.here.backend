package com.drop.here.backend.drophere.product.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCustomizationWrapperRequest {

    @NotBlank
    @Length(max = 255)
    @ApiModelProperty(value = "Customization heading", example = "Roll type", required = true)
    private String heading;

    @NotBlank
    @ApiModelProperty(value = "Customization type", example = "SINGLE", required = true)
    private String type;

    @NotNull
    @ApiModelProperty(value = "Is required to order", example = "true", required = true)
    private boolean required;

    @NotEmpty
    @NotNull
    @Valid
    @ApiModelProperty(value = "Customizations list", required = true)
    private List<@Valid ProductCustomizationRequest> customizations;

}
