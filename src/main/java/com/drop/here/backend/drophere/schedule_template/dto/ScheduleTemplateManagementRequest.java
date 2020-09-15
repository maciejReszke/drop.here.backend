package com.drop.here.backend.drophere.schedule_template.dto;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTemplateManagementRequest {

    @ApiModelProperty(value = "Schedule template name", example = "SCHODZI JAK JA PIER", required = true)
    @NotBlank
    @Length(max = 255)
    private String name;

    @Valid
    @ApiModelProperty(value = "Schedule template name")
    private List<@Valid ScheduleTemplateProductRequest> scheduleTemplateProducts;
}
