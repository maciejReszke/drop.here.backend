package com.drop.here.backend.drophere.schedule_template.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateShortResponse;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import com.drop.here.backend.drophere.schedule_template.repository.ScheduleTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class ScheduleTemplateStoreService {
    private final ScheduleTemplateRepository scheduleTemplateRepository;

    // todo bylo transactional(rollbackFor = Exception.class)
    public Mono<Void> deleteScheduleTemplateProductByProduct(Product product) {
        scheduleTemplateRepository.deleteScheduleTemplateProductByProduct(product);
    }

    public Optional<ScheduleTemplate> findByIdAndCompany(Long scheduleTemplateId, Company company) {
        return scheduleTemplateRepository.findByIdAndCompany(scheduleTemplateId, company);
    }

    public void save(ScheduleTemplate scheduleTemplate) {
        scheduleTemplateRepository.save(scheduleTemplate);
    }

    public void delete(ScheduleTemplate scheduleTemplate) {
        scheduleTemplateRepository.delete(scheduleTemplate);
    }

    public List<ScheduleTemplateShortResponse> findByCompany(Company company) {
        return scheduleTemplateRepository.findByCompany(company);
    }

    public Optional<ScheduleTemplate> findByIdAndCompanyWithScheduleTemplateProducts(Long scheduleTemplateId, Company company) {
        return scheduleTemplateRepository.findByIdAndCompanyWithScheduleTemplateProducts(scheduleTemplateId, company);
    }
}
