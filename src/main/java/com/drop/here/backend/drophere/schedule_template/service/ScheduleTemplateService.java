package com.drop.here.backend.drophere.schedule_template.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateResponse;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateShortResponse;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import com.drop.here.backend.drophere.schedule_template.repository.ScheduleTemplateRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleTemplateService {
    private final ScheduleTemplateMappingService scheduleTemplateMappingService;
    private final ScheduleTemplateRepository scheduleTemplateRepository;

    public ResourceOperationResponse createTemplate(String companyUid, ScheduleTemplateManagementRequest scheduleTemplateManagementRequest, AccountAuthentication accountAuthentication) {
        final ScheduleTemplate scheduleTemplate = scheduleTemplateMappingService.toScheduleTemplate(scheduleTemplateManagementRequest, accountAuthentication.getCompany());
        log.info("Saving schedule template for company {} with name {}", companyUid, scheduleTemplateManagementRequest.getName());
        scheduleTemplateRepository.save(scheduleTemplate);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, scheduleTemplate.getId());
    }

    public ResourceOperationResponse updateTemplate(String companyUid, Long scheduleTemplateId, ScheduleTemplateManagementRequest scheduleTemplateManagementRequest, AccountAuthentication accountAuthentication) {
        final ScheduleTemplate scheduleTemplate = findByIdAndCompany(scheduleTemplateId, accountAuthentication.getCompany());
        scheduleTemplateMappingService.updateScheduleTemplate(scheduleTemplate, scheduleTemplateManagementRequest, accountAuthentication.getCompany());
        log.info("Saving schedule template for company {} with name {} id {}", companyUid, scheduleTemplateManagementRequest.getName(), scheduleTemplateId);
        scheduleTemplateRepository.save(scheduleTemplate);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, scheduleTemplateId);
    }

    private ScheduleTemplate findByIdAndCompany(Long scheduleTemplateId, Company company) {
        return scheduleTemplateRepository.findByIdAndCompany(scheduleTemplateId, company)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Schedule template for company %s with id %s was not found", company.getUid(), scheduleTemplateId),
                        RestExceptionStatusCode.SCHEDULE_TEMPLATE_BY_ID_AND_COMPANY_NOT_FOUND
                ));
    }

    public ResourceOperationResponse deleteTemplate(String companyUid, Long scheduleTemplateId, AccountAuthentication accountAuthentication) {
        final ScheduleTemplate scheduleTemplate = findByIdAndCompany(scheduleTemplateId, accountAuthentication.getCompany());
        log.info("Deleting schedule template for company {} with name {} id {}", companyUid, scheduleTemplate.getName(), scheduleTemplateId);
        scheduleTemplateRepository.delete(scheduleTemplate);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, scheduleTemplateId);
    }

    public ScheduleTemplateResponse findById(Long scheduleTemplateId, AccountAuthentication accountAuthentication) {
        return scheduleTemplateMappingService.toTemplateResponse(findByIdAndCompany(scheduleTemplateId, accountAuthentication.getCompany()));
    }


    public List<ScheduleTemplateShortResponse> findTemplates(AccountAuthentication accountAuthentication) {
        return scheduleTemplateRepository.findByCompany(accountAuthentication.getCompany())
                .stream()
                .map(scheduleTemplateMappingService::toTemplateShortResponse)
                .sorted(Comparator.comparing(ScheduleTemplateShortResponse::getName, String::compareToIgnoreCase))
                .collect(Collectors.toList());
    }
}
