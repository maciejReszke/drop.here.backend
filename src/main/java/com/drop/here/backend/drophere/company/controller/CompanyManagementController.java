package com.drop.here.backend.drophere.company.controller;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.company.dto.CompanyCustomerRelationshipManagementRequest;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyCustomerResponse;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management/companies")
@Api(tags = "Company management API")
public class CompanyManagementController {
    private final CompanyService companyService;

    private static final String IMAGE_PART_NAME = "image";

    @GetMapping
    @ApiOperation("Get own company info")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Own company info"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<CompanyManagementResponse> findOwnCompany(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return accountAuthenticationMono.flatMap(companyService::findOwnCompany);
    }

    @PutMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update company")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Company updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> updateCompany(@ApiIgnore Mono<AccountAuthentication> authenticationMono,
                                                         @RequestBody @Valid Mono<CompanyManagementRequest> companyManagementRequestMono) {
        return authenticationMono.zipWith(companyManagementRequestMono)
                .flatMap(tuple -> companyService.updateCompany(tuple.getT2(), tuple.getT1()));
    }

    @PostMapping("/images")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update company image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("hasAuthority('" + PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE + "')")
    public Mono<ResourceOperationResponse> updateCompanyImage(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                              @RequestPart(name = IMAGE_PART_NAME) Mono<FilePart> imageMono) {
        return accountAuthenticationMono.zipWith(imageMono)
                .flatMap(tuple -> companyService.updateImage(tuple.getT2(), tuple.getT1()));
    }

    @GetMapping("/customers")
    @ApiOperation("Get company customers")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Company customers"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Flux<CompanyCustomerResponse> findCustomers(@ApiIgnore Mono<AccountAuthentication> authenticationMono,
                                                       @ApiParam(value = "Customer name (starting with name or starting with surname)")
                                                       @RequestParam(value = "customerName") String desiredCustomerStartingSubstring,
                                                       @ApiParam(value = "Is customer blocked (globally)")
                                                       @RequestParam(value = "blocked", required = false) Boolean blocked,
                                                       Pageable pageable) {
        return authenticationMono.flatMapMany(accountAuthentication ->
                companyService.findCustomers(desiredCustomerStartingSubstring, blocked, accountAuthentication, pageable));
    }

    @PutMapping("/customers/{customerId}")
    @ApiOperation("Update companies customer relationship")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isCompaniesCustomer(authentication, #customerId)")
    public Mono<ResourceOperationResponse> updateCustomerRelationship(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                                      @ApiIgnore @PathVariable Long customerId,
                                                                      @RequestBody @Valid Mono<CompanyCustomerRelationshipManagementRequest> customerRelationshipManagementRequestMono) {
        return accountAuthenticationMono.zipWith(customerRelationshipManagementRequestMono)
                .flatMap(tuple -> companyService.updateCustomerRelationship(customerId, tuple.getT2(), tuple.getT1()));
    }
}
