package com.drop.here.backend.drophere.schedule_template.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ScheduleTemplateShortResponse {

    @ApiModelProperty(value = "Schedule template id", example = "5")
    private Long id;

    @ApiModelProperty(value = "Schedule template name", example = "Best seler")
    private String name;

    @ApiModelProperty(value = "Amount of products in template", example = "15")
    private int productsAmount;

}
