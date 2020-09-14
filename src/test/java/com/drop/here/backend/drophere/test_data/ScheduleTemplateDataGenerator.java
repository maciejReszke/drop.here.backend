package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ScheduleTemplateDataGenerator {

    public ScheduleTemplateManagementRequest request(int i) {
        return ScheduleTemplateManagementRequest.builder()
                .name("name" + i)
                .build();
    }

    public ScheduleTemplate scheduleTemplate(int i, Company company) {
        return ScheduleTemplate.builder()
                .name("name" + i)
                .createdAt(LocalDateTime.now())
                .company(company)
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }
}
