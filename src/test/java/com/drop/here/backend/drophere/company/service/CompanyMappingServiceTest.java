package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryService;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyMappingServiceTest {

    @InjectMocks
    private CompanyMappingService companyMappingService;

    @Mock
    private CountryService countryService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(companyMappingService, "randomUidPart", 4, true);
    }

    @Test
    void givenCompanyWhenToManagementResponseThenMap() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final Country country = CountryDataGenerator.poland();
        final Company company = CompanyDataGenerator.company(1, account, country);

        //when
        final CompanyManagementResponse result = companyMappingService.toManagementResponse(company);

        //then
        assertThat(result.getCountry()).isEqualTo(country.getName());
        assertThat(result.getName()).isEqualTo(company.getName());
        assertThat(result.getUid()).isEqualTo(company.getUid());
        assertThat(result.getVisibilityStatus()).isEqualTo(company.getVisibilityStatus());
        assertThat(result.isRegistered()).isTrue();
    }

    @Test
    void givenNullCompanyWhenToManagementResponseThenMap() {
        //when
        final CompanyManagementResponse result = companyMappingService.toManagementResponse(null);

        //then
        assertThat(result.getCountry()).isNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getUid()).isNull();
        assertThat(result.getVisibilityStatus()).isNull();
        assertThat(result.isRegistered()).isFalse();
    }

    @Test
    void givenCompanyRequestWhenCreateCompanyThenMap() {
        //given
        final CompanyManagementRequest companyManagementRequest = CompanyDataGenerator.managementRequest(1);
        companyManagementRequest.setName("Glodny maciek");
        final Account account = Account.builder().build();
        final Country country = CountryDataGenerator.poland();

        when(countryService.findActive(companyManagementRequest.getCountry()))
                .thenReturn(country);
        //when
        final Company company = companyMappingService.createCompany(companyManagementRequest, account);

        //then
        assertThat(company.getUid()).startsWith("glodny-maciek");
        assertThat(company.getUid()).hasSize("glodny-maciek".length() + 4);
        assertThat(company.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(company.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(company.getVisibilityStatus()).isEqualTo(CompanyVisibilityStatus.VISIBLE);
        assertThat(company.getName()).isEqualTo(companyManagementRequest.getName());
        assertThat(company.getAccount()).isEqualTo(account);
        assertThat(company.getCountry()).isEqualTo(country);
    }

    @Test
    void givenCompanyRequestWhenUpdateCompanyThenMap() {
        //given
        final CompanyManagementRequest companyManagementRequest = CompanyDataGenerator.managementRequest(1);
        companyManagementRequest.setName("Glodny maciek");
        final Country country = CountryDataGenerator.poland();
        final Company company = Company.builder().build();

        when(countryService.findActive(companyManagementRequest.getCountry()))
                .thenReturn(country);
        //when
        companyMappingService.updateCompany(companyManagementRequest, company);

        //then
        assertThat(company.getUid()).startsWith("glodny-maciek");
        assertThat(company.getUid()).hasSize("glodny-maciek".length() + 4);
        assertThat(company.getCreatedAt()).isNull();
        assertThat(company.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(company.getVisibilityStatus()).isEqualTo(CompanyVisibilityStatus.VISIBLE);
        assertThat(company.getName()).isEqualTo(companyManagementRequest.getName());
        assertThat(company.getAccount()).isNull();
        assertThat(company.getCountry()).isEqualTo(country);
    }

}