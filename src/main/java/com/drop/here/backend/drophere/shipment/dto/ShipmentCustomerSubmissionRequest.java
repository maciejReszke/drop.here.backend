package com.drop.here.backend.drophere.shipment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentCustomerSubmissionRequest {

    @ApiModelProperty(value = "Products", required = true)
    @NotEmpty
    @NotNull
    @Valid
    private List<@Valid ShipmentProductRequest> products;

    @Length(max = 2048)
    @ApiModelProperty(value = "Dzien dobry, prosze wypelnic pączka miłością")
    private String comment;
}
