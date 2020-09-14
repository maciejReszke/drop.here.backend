package com.drop.here.backend.drophere.schedule_template.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateResponse;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateShortResponse;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import org.springframework.stereotype.Service;

@Service
public class ScheduleTemplateMappingService {

    // TODO: 14/09/2020 test, implmenet
    public ScheduleTemplate toScheduleTemplate(ScheduleTemplateManagementRequest scheduleTemplateManagementRequest, Company company) {
        return null;
    }

    // TODO: 14/09/2020 test, implement
    public void updateScheduleTemplate(ScheduleTemplate scheduleTemplate, ScheduleTemplateManagementRequest scheduleTemplateManagementRequest) {

    }

    // TODO: 14/09/2020
    public ScheduleTemplateResponse toTemplateResponse(ScheduleTemplate scheduleTemplate) {
        return null;
    }

    // TODO: 14/09/2020 test
    public ScheduleTemplateShortResponse toTemplateShortResponse(ScheduleTemplate scheduleTemplate) {
        return ScheduleTemplateShortResponse.builder()
                .name(scheduleTemplate.getName())
                .id(scheduleTemplate.getId())
                .productsAmount(scheduleTemplate.getScheduleTemplateProducts().size())
                .build();
    }
}
