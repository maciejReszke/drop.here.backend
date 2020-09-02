package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.controller.CompanyCustomerResponse;
import com.drop.here.backend.drophere.company.dto.CompanyCustomerRelationshipManagementRequest;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerService;
import com.drop.here.backend.drophere.drop.service.DropMembershipService;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyValidationService companyValidationService;
    private final CompanyMappingService companyMappingService;
    private final PrivilegeService privilegeService;
    private final ImageService imageService;
    private final DropMembershipService dropMembershipService;
    private final CompanyCustomerBlockingService companyCustomerBlockingService;
    private final CustomerService customerService;

    public boolean isVisible(String companyUid) {
        return findByUid(companyUid)
                .map(company -> company.getVisibilityStatus() == CompanyVisibilityStatus.VISIBLE)
                .orElse(false);
    }

    private Optional<Company> findByUid(String companyUid) {
        return companyRepository.findByUid(companyUid);
    }

    private Company getByUid(String companyUid) {
        return findByUid(companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Company with uid %s was not found", companyUid),
                        RestExceptionStatusCode.COMPANY_BY_UID_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public CompanyManagementResponse findOwnCompany(AccountAuthentication authentication) {
        final Company company = companyRepository.findByAccount(authentication.getPrincipal())
                .orElse(null);
        return companyMappingService.toManagementResponse(company);
    }

    public ResourceOperationResponse updateCompany(CompanyManagementRequest companyManagementRequest, AccountAuthentication authentication) {
        companyValidationService.validate(companyManagementRequest);
        return authentication.getCompany() == null
                ? createCompany(companyManagementRequest, authentication)
                : updateCompany(companyManagementRequest, authentication.getCompany());
    }

    private ResourceOperationResponse updateCompany(CompanyManagementRequest companyManagementRequest, Company company) {
        companyMappingService.updateCompany(companyManagementRequest, company);
        log.info("Updating company with uid {}", company.getUid());
        companyRepository.save(company);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, company.getId());
    }

    private ResourceOperationResponse createCompany(CompanyManagementRequest companyManagementRequest, AccountAuthentication authentication) {
        final Company company = companyMappingService.createCompany(companyManagementRequest, authentication.getPrincipal());
        log.info("Creating new company with uid {} for account with id {}", company.getUid(), authentication.getPrincipal().getId());
        companyRepository.save(company);
        privilegeService.addCompanyCreatedPrivilege(authentication.getPrincipal());
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, company.getId());
    }

    @Transactional
    public ResourceOperationResponse updateImage(MultipartFile imagePart, AccountAuthentication authentication) {
        try {
            final Image image = imageService.createImage(imagePart.getBytes(), ImageType.COMPANY_IMAGE);
            final Company company = authentication.getCompany();
            company.setImage(image);
            log.info("Updating image for company {}", company.getUid());
            companyRepository.save(company);
            return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, company.getId());
        } catch (IOException exception) {
            throw new RestIllegalRequestValueException("Invalid image " + exception.getMessage(),
                    RestExceptionStatusCode.UPDATE_COMPANY_IMAGE_INVALID_IMAGE);
        }
    }

    @Transactional(readOnly = true)
    public Image findImage(String companyUid) {
        return companyRepository.findByUidWithImage(companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Image for company %s was not found", companyUid),
                        RestExceptionStatusCode.COMPANY_IMAGE_WAS_NOT_FOUND))
                .getImage();
    }

    public boolean hasRelation(Company company, Long customerId) {
        return dropMembershipService.existsMembership(company, customerId);
    }

    // TODO: 02/09/2020 test, implemetn
    public Page<CompanyCustomerResponse> findCustomers(String desiredCustomerStartingSubstring, Boolean blocked, AccountAuthentication authentication) {
        return null;
    }

    public ResourceOperationResponse updateCustomerRelationship(Long customerId, CompanyCustomerRelationshipManagementRequest companyCustomerManagementRequest, AccountAuthentication accountAuthentication) {
        final Customer customer = customerService.findById(customerId);
        companyCustomerBlockingService.handleCustomerBlocking(companyCustomerManagementRequest.isBlock(), customer, accountAuthentication.getCompany());
        log.info("Updated customer {} with company relation {}", customer, accountAuthentication.getCompany().getUid());
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, customerId);
    }

    public boolean isBlocked(String companyUid, Customer customer) {
        final Company company = getByUid(companyUid);
        return companyCustomerBlockingService.isBlocked(company, customer);
    }
}
