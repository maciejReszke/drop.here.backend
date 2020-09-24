package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.AccountPersistenceService;
import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.dto.CompanyCustomerRelationshipManagementRequest;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyCustomerResponse;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import com.drop.here.backend.drophere.company.repository.CompanyRepository;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerStoreService;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.spot.service.SpotMembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyValidationService companyValidationService;
    private final CompanyMappingService companyMappingService;
    private final PrivilegeService privilegeService;
    private final ImageService imageService;
    private final SpotMembershipService spotMembershipService;
    private final CompanyCustomerRelationshipService companyCustomerRelationshipService;
    private final CustomerStoreService customerStoreService;
    private final CompanyCustomerSearchingService companyCustomerSearchingService;
    private final AccountPersistenceService accountPersistenceService;

    public Mono<Company> getVisible(String companyUid) {
        return findByUid(companyUid)
                .filter(company -> company.getVisibilityStatus() == CompanyVisibilityStatus.VISIBLE);
    }

    private Mono<Company> findByUid(String companyUid) {
        return companyRepository.findByUid(companyUid);
    }

    private Mono<Company> getByUid(String companyUid) {
        return findByUid(companyUid)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Company with uid %s was not found", companyUid),
                        RestExceptionStatusCode.COMPANY_BY_UID_NOT_FOUND)));
    }

    public Mono<CompanyManagementResponse> findOwnCompany(AccountAuthentication authentication) {
        return companyRepository.findByAccount(authentication.getPrincipal())
                .map(companyMappingService::toManagementResponse)
                .switchIfEmpty(Mono.defer((() -> Mono.just(companyMappingService.toManagementResponse(null)))));
    }

    public Mono<ResourceOperationResponse> updateCompany(CompanyManagementRequest companyManagementRequest, AccountAuthentication authentication) {
        companyValidationService.validate(companyManagementRequest);
        return authentication.getCompany() == null
                ? createCompany(companyManagementRequest, authentication)
                : updateCompany(companyManagementRequest, authentication.getCompany());
    }

    private Mono<ResourceOperationResponse> updateCompany(CompanyManagementRequest companyManagementRequest, Company company) {
        companyMappingService.updateCompany(companyManagementRequest, company);
        log.info("Updating company with uid {}", company.getUid());
        return companyRepository.save(company)
                .map(savedCompany -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, company.getId()));
    }

    // TODO: 24/09/2020 transakcja!
    private Mono<ResourceOperationResponse> createCompany(CompanyManagementRequest companyManagementRequest, AccountAuthentication authentication) {
        final Company company = companyMappingService.createCompany(companyManagementRequest, authentication.getPrincipal());
        log.info("Creating new company with uid {} for account with id {}", company.getUid(), authentication.getPrincipal().getId());
        final Account account = authentication.getPrincipal();
        privilegeService.addCompanyCreatedPrivilege(account);
        return accountPersistenceService.updateAccount(account)
                .flatMap(saved -> companyRepository.save(company))
                .map(saved -> new ResourceOperationResponse(ResourceOperationStatus.CREATED, company.getId()));
    }

    public Mono<ResourceOperationResponse> updateImage(FilePart imagePart, AccountAuthentication authentication) {
        final Company company = authentication.getCompany();
        return imageService.createImage(imagePart, ImageType.COMPANY_IMAGE, company.getId())
                .doOnNext(image -> log.info("Updating image for company {}", company.getUid()))
                .map(image -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, company.getId()));
    }

    public Mono<Image> findImage(String companyUid) {
        return companyRepository.findByUid(companyUid)
                .flatMap(company -> imageService.findImage(company.getId(), ImageType.COMPANY_IMAGE));
    }

    public boolean hasRelation(Company company, Long customerId) {
        return spotMembershipService.existsMembership(company, customerId) ||
                companyCustomerRelationshipService.hasRelationship(company, customerId);
    }

    public Flux<CompanyCustomerResponse> findCustomers(String desiredCustomerStartingSubstring, Boolean blocked, AccountAuthentication authentication, Pageable pageable) {
        return companyCustomerSearchingService.findCustomers(desiredCustomerStartingSubstring, blocked, authentication, pageable);
    }

    public Mono<ResourceOperationResponse> updateCustomerRelationship(String customerId, CompanyCustomerRelationshipManagementRequest companyCustomerManagementRequest, AccountAuthentication accountAuthentication) {
        return customerStoreService.findById(customerId)
                .doOnNext(customer -> log.info("Updating customer {} with company relation {}", customer, accountAuthentication.getCompany().getUid()))
                .flatMap(customer -> companyCustomerRelationshipService.handleCustomerBlocking(companyCustomerManagementRequest.isBlock(), customer, accountAuthentication.getCompany()))
                .thenReturn(new ResourceOperationResponse(ResourceOperationStatus.UPDATED, customerId));
    }

    public Mono<Boolean> isBlocked(String companyUid, Customer customer) {
        return getByUid(companyUid)
                .flatMap(company -> companyCustomerRelationshipService.isBlocked(company, customer))
                .switchIfEmpty(Mono.just(false));
    }
}
