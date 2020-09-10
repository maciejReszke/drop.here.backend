package com.drop.here.backend.drophere.company;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.dto.CompanyCustomerRelationshipManagementRequest;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.company.service.CompanyCustomerRelationshipService;
import com.drop.here.backend.drophere.company.service.CompanyMappingService;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.company.service.CompanyValidationService;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerStoreService;
import com.drop.here.backend.drophere.drop.service.DropMembershipService;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
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

    @Mock
    private PrivilegeService privilegeService;

    @Mock
    private ImageService imageService;

    @Mock
    private DropMembershipService dropMembershipService;

    @Mock
    private CompanyCustomerRelationshipService companyCustomerRelationshipService;

    @Mock
    private CustomerStoreService customerStoreService;

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
        verifyNoInteractions(privilegeService);
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
        doNothing().when(privilegeService).addCompanyCreatedPrivilege(account);

        //when
        final ResourceOperationResponse result = companyService.updateCompany(request, accountAuthentication);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenImageWhenUpdateImageThenUpdate() throws IOException {
        //given
        final MockMultipartFile image = new MockMultipartFile("name", "byte".getBytes());
        final Account account = Account.builder().build();
        final Company company = Company.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .company(company)
                .build();

        final Image imageEntity = Image.builder().build();
        when(imageService.createImage(image.getBytes(), ImageType.COMPANY_IMAGE))
                .thenReturn(imageEntity);
        when(companyRepository.save(company)).thenReturn(company);

        //when
        final ResourceOperationResponse resourceOperationResponse = companyService.updateImage(image, accountAuthentication);

        //then
        assertThat(resourceOperationResponse.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(company.getImage()).isEqualTo(imageEntity);
    }

    @Test
    void givenExistingCustomerWithImageWhenFindImageThenFind() {
        //given
        final String companyUid = "companyUid";
        final Image image = Image.builder().build();
        final Company company = Company.builder()
                .image(image)
                .build();

        when(companyRepository.findByUidWithImage(companyUid)).thenReturn(Optional.of(company));
        //when
        final Image result = companyService.findImage(companyUid);

        //then
        assertThat(result).isEqualTo(image);
    }

    @Test
    void givenNotExistingCustomerWithImageWhenFindImageThenError() {
        //given
        final String companyUid = "companyUid";

        when(companyRepository.findByUidWithImage(companyUid)).thenReturn(Optional.empty());
        //when
        final Throwable throwable = catchThrowable(() -> companyService.findImage(companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingDropMembershipWhenHasRelationThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(dropMembershipService.existsMembership(company, customerId)).thenReturn(true);

        //when
        final boolean result = companyService.hasRelation(company, customerId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenExistingCompanyCustomerRelationshipWhenHasRelationThenTrue() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(dropMembershipService.existsMembership(company, customerId)).thenReturn(false);
        when(companyCustomerRelationshipService.hasRelationship(company, customerId)).thenReturn(true);

        //when
        final boolean result = companyService.hasRelation(company, customerId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotExistingMembershipNorRelationshipWhenHasRelationThenFalse() {
        //given
        final Company company = Company.builder().build();
        final Long customerId = 5L;

        when(dropMembershipService.existsMembership(company, customerId)).thenReturn(false);
        when(companyCustomerRelationshipService.hasRelationship(company, customerId)).thenReturn(false);

        //when
        final boolean result = companyService.hasRelation(company, customerId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void givenExistingCompanyForBlockedCustomerWhenIsBlockedThenTrue() {
        //given
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder().build();

        when(companyRepository.findByUid(companyUid)).thenReturn(Optional.of(company));
        when(companyCustomerRelationshipService.isBlocked(company, customer)).thenReturn(true);

        //when
        final boolean blocked = companyService.isBlocked(companyUid, customer);

        //then
        assertThat(blocked).isTrue();
    }

    @Test
    void givenExistingCompanyForNotBlockedCustomerWhenIsBlockedThenFalse() {
        //given
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();
        final Company company = Company.builder().build();

        when(companyRepository.findByUid(companyUid)).thenReturn(Optional.of(company));
        when(companyCustomerRelationshipService.isBlocked(company, customer)).thenReturn(false);

        //when
        final boolean blocked = companyService.isBlocked(companyUid, customer);

        //then
        assertThat(blocked).isFalse();
    }

    @Test
    void givenNotExistingCompanyWhenIsBlockedThenThrowException() {
        //given
        final String companyUid = "companyUid";
        final Customer customer = Customer.builder().build();

        when(companyRepository.findByUid(companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> companyService.isBlocked(companyUid, customer));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenValidRequestWhenUpdateCustomerRelationshipThenUpdate() {
        //given
        final Long customerId = 1L;
        final CompanyCustomerRelationshipManagementRequest companyCustomerRelationshipManagementRequest =
                CompanyCustomerRelationshipManagementRequest.builder().block(true).build();
        final Company company = Company.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .company(company)
                .build();
        final Customer customer = Customer.builder().build();

        when(customerStoreService.findById(customerId)).thenReturn(customer);
        doNothing().when(companyCustomerRelationshipService).handleCustomerBlocking(true, customer, company);

        //when
        final ResourceOperationResponse response = companyService.updateCustomerRelationship(customerId, companyCustomerRelationshipManagementRequest, accountAuthentication);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }
}