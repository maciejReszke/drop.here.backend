package com.drop.here.backend.drophere.schedule_template.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class ScheduleTemplateResponse {

    @ApiModelProperty(value = "Schedule template id", example = "5")
    Long id;

    @ApiModelProperty(value = "Schedule template name", example = "Best seler")
    String name;

    @ApiModelProperty(value = "Schedule template products")
    List<ScheduleTemplateProductResponse> scheduleTemplateProducts;

    @ApiModelProperty(value = "Amount of products in template", example = "15")
    Integer productsAmount;
}