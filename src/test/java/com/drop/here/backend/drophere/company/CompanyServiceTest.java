package com.drop.here.backend.drophere.company;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.company.service.CompanyMappingService;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.company.service.CompanyValidationService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyValidationService companyValidationService;

    @Mock
    private CompanyMappingService companyMappingService;

    @Test
    void givenVisibleCompanyWhenIsVisibleThenTrue() {
        //given
        final String uid = "uid";
        when(companyRepository.findByUid(uid)).thenReturn(Optional.of(Company.builder()
                .visibilityStatus(CompanyVisibilityStatus.VISIBLE)
                .build()));

        //when
        final boolean result = companyService.isVisible(uid);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotVisibleCompanyWhenIsVisibleThenFalse() {
        //given
        final String uid = "uid";
        when(companyRepository.findByUid(uid)).thenReturn(Optional.of(Company.builder()
                .visibilityStatus(CompanyVisibilityStatus.HIDDEN)
                .build()));

        //when
        final boolean result = companyService.isVisible(uid);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenNotExistingCompanyWhenIsVisibleThenFalse() {
        //given
        final String uid = "uid";
        when(companyRepository.findByUid(uid)).thenReturn(Optional.empty());

        //when
        final boolean result = companyService.isVisible(uid);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenAccountAuthenticationWhenFindOwnCompanyThenFind() {
        //given
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();
        final CompanyManagementResponse companyManagementResponse = CompanyManagementResponse.builder().build();

        when(companyMappingService.toManagementResponse(accountAuthentication.getCompany()))
                .thenReturn(companyManagementResponse);

        //when
        final CompanyManagementResponse response = companyService.findOwnCompany(accountAuthentication);

        //then
        assertThat(response).isEqualTo(companyManagementResponse);
    }

    @Test
    void givenExistingCompanyWhenUpdateCompanyThenUpdate() {
        //given
        final CompanyManagementRequest request = CompanyDataGenerator.managementRequest(1);
        final Company company = Company.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .company(company)
                .build();

        doNothing().when(companyValidationService).validate(request);
        doNothing().when(companyMappingService).updateCompany(request, company);
        when(companyRepository.save(company)).thenReturn(company);
        //when
        final ResourceOperationResponse result = companyService.updateCompany(request, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenNotExistingCompanyWhenUpdateCompanyThenCreate() {
        //given
        final CompanyManagementRequest request = CompanyDataGenerator.managementRequest(1);
        final Account account = Account.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .build();
        final Company company = Company.builder().build();

        doNothing().when(companyValidationService).validate(request);
        when(companyMappingService.createCompany(request, account)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);

        //when
        final ResourceOperationResponse result = companyService.updateCompany(request, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }
}