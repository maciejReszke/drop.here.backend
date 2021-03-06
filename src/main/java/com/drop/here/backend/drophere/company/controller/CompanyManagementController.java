package com.drop.here.backend.drophere.company.controller;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.company.dto.CompanyCustomerRelationshipManagementRequest;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.dto.response.CompanyCustomerResponse;
import com.drop.here.backend.drophere.company.dto.response.CompanyManagementResponse;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management/companies")
@Api(tags = "Company management API")
public class CompanyManagementController {
    private final CompanyService companyService;

    private static final String IMAGE_PART_NAME = "image";

    @GetMapping
    @ApiOperation(value = "Get own company info", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Own company info"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public CompanyManagementResponse findOwnCompany(@ApiIgnore AccountAuthentication authentication) {
        return companyService.findOwnCompany(authentication);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update company", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Company updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse updateCompany(@ApiIgnore AccountAuthentication authentication,
                                                   @RequestBody @Valid CompanyManagementRequest companyManagementRequest) {
        return companyService.updateCompany(companyManagementRequest, authentication);
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update company image", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Image updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("hasAuthority('" + PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE + "')")
    public ResourceOperationResponse updateCompanyImage(@ApiIgnore AccountAuthentication authentication,
                                                        @RequestPart(name = IMAGE_PART_NAME) MultipartFile image) {
        return companyService.updateImage(image, authentication);
    }

    @GetMapping("/customers")
    @ApiOperation(value = "Get company customers", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Company customers"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Page<CompanyCustomerResponse> findCustomers(@ApiIgnore AccountAuthentication authentication,
                                                       @ApiParam(value = "Customer name (starting with name or starting with surname)",
                                                               required = true)
                                                       @RequestParam(value = "customerName") String desiredCustomerStartingSubstring,
                                                       @ApiParam(value = "Is customer blocked (globally)")
                                                       @RequestParam(value = "blocked", required = false) Boolean blocked,
                                                       Pageable pageable) {
        return companyService.findCustomers(desiredCustomerStartingSubstring, blocked, authentication, pageable);
    }

    @PutMapping("/customers/{customerId}")
    @ApiOperation(value = "Update companies customer relationship", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Customer updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isCompaniesCustomer(authentication, #customerId)")
    public ResourceOperationResponse updateCustomerRelationship(@ApiIgnore AccountAuthentication authentication,
                                                                @ApiIgnore @PathVariable Long customerId,
                                                                @RequestBody @Valid CompanyCustomerRelationshipManagementRequest companyCustomerManagementRequest) {
        return companyService.updateCustomerRelationship(customerId, companyCustomerManagementRequest, authentication);
    }
}
