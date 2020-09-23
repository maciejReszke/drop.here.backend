package com.drop.here.backend.drophere.schedule_template.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateResponse;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.ScheduleTemplateDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleTemplateServiceTest {

    @InjectMocks
    private ScheduleTemplateService scheduleTemplateService;

    @Mock
    private ScheduleTemplateMappingService scheduleTemplateMappingService;

    @Mock
    private ScheduleTemplateStoreService scheduleTemplateStoreService;

    @Test
    void givenRequestWhenCreateTemplateThenCreate() {
        //given
        final String companyUid = "companyUid";
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateDataGenerator.request(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final ScheduleTemplate template = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);

        when(scheduleTemplateMappingService.toScheduleTemplate(scheduleTemplateManagementRequest, company))
                .thenReturn(template);
        doNothing().when(scheduleTemplateStoreService).save(template);

        //when
        final ResourceOperationResponse result = scheduleTemplateService.createTemplate(companyUid, scheduleTemplateManagementRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingScheduleTemplateWhenUpdateTemplateThenUpdate() {
        //given
        final String companyUid = "companyUid";
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateDataGenerator.request(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long templateId = 15L;
        final ScheduleTemplate template = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);

        when(scheduleTemplateStoreService.findByIdAndCompany(templateId, company)).thenReturn(Optional.of(template));
        doNothing().when(scheduleTemplateMappingService).updateScheduleTemplate(template, scheduleTemplateManagementRequest, company);
        doNothing().when(scheduleTemplateStoreService).save(template);

        //when
        final ResourceOperationResponse result = scheduleTemplateService.updateTemplate(companyUid, templateId, scheduleTemplateManagementRequest, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenNotExistingScheduleTemplateWhenUpdateTemplateThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final ScheduleTemplateManagementRequest scheduleTemplateManagementRequest = ScheduleTemplateDataGenerator.request(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long templateId = 15L;

        when(scheduleTemplateStoreService.findByIdAndCompany(templateId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> scheduleTemplateService.updateTemplate(companyUid, templateId, scheduleTemplateManagementRequest, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingScheduleTemplateWhenDeleteTemplateThenDelete() {
        //given
        final String companyUid = "companyUid";
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long templateId = 15L;
        final ScheduleTemplate template = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);

        when(scheduleTemplateStoreService.findByIdAndCompany(templateId, company)).thenReturn(Optional.of(template));
        doNothing().when(scheduleTemplateStoreService).delete(template);

        //when
        final ResourceOperationResponse result = scheduleTemplateService.deleteTemplate(companyUid, templateId, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }

    @Test
    void givenNotExistingScheduleTemplateWhenDeleteTemplateThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long templateId = 15L;

        when(scheduleTemplateStoreService.findByIdAndCompany(templateId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> scheduleTemplateService.deleteTemplate(companyUid, templateId, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingScheduleTemplateWhenFindByIdThenFind() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long templateId = 15L;
        final ScheduleTemplateResponse scheduleTemplateResponse = ScheduleTemplateResponse.builder().build();
        final ScheduleTemplate template = ScheduleTemplateDataGenerator.scheduleTemplate(1, company);

        when(scheduleTemplateStoreService.findByIdAndCompanyWithScheduleTemplateProducts(templateId, company)).thenReturn(Optional.of(template));
        when(scheduleTemplateMappingService.toTemplateResponse(template)).thenReturn(scheduleTemplateResponse);

        //when
        final ScheduleTemplateResponse result = scheduleTemplateService.findById(templateId, accountAuthentication);

        //then
        assertThat(result).isEqualTo(scheduleTemplateResponse);
    }

    @Test
    void givenNotExistingScheduleTemplateWhenFindByIdThenThrowException() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final Company company = CompanyDataGenerator.company(1, account, null);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long templateId = 15L;

        when(scheduleTemplateStoreService.findByIdAndCompanyWithScheduleTemplateProducts(templateId, company)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> scheduleTemplateService.findById(templateId, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }
}