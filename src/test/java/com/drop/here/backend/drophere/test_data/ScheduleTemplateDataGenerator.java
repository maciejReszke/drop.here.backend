package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateProductRequest;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// TODO: 25/09/2020 out
@UtilityClass
public class ScheduleTemplateDataGenerator {

    public ScheduleTemplateManagementRequest request(int i) {
        return ScheduleTemplateManagementRequest.builder()
                .name("name" + i)
                .scheduleTemplateProducts(List.of(productRequest(2 * i), productRequest(2 * i + 1)))
                .build();
    }

    private ScheduleTemplateProductRequest productRequest(int i) {
        return ScheduleTemplateProductRequest.builder()
                .amount(i + 4)
                .limitedAmount(true)
                .productId(5L + i)
                .price(BigDecimal.valueOf(55L))
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
