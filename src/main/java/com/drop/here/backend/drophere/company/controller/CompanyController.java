package com.drop.here.backend.drophere.company.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.company.dto.CompanyCustomerRelationshipManagementRequest;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
@Api(tags = "Company customer API")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/{companyUid}/images")
    @ApiOperation("Get company image")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Company image"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid) or " +
            "@authenticationPrivilegesService.isCompanyVisibleForCustomer(authentication, #companyUid)")
    public ResponseEntity<byte[]> findImage(@ApiIgnore AccountAuthentication authentication,
                                            @ApiIgnore @PathVariable String companyUid) {
        final Image image = companyService.findImage(companyUid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .eTag(companyUid + image.getId())
                .contentType(MediaType.IMAGE_JPEG)
                .body(image.getBytes());
    }

    // TODO: 02/09/2020 test
    @GetMapping("/{companyUid}/customers")
    @ApiOperation("Get company customers")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Company customers"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Page<CompanyCustomerResponse> findCustomers(@ApiIgnore AccountAuthentication authentication,
                                                       @ApiIgnore @PathVariable String companyUid,
                                                       @ApiParam(value = "Customer name (starting with name or starting with surname)")
                                                       @RequestParam(value = "customerName", required = false) String desiredCustomerStartingSubstring,
                                                       @ApiParam(value = "Is customer blocked (globally)")
                                                       @RequestParam(value = "blocked", required = false) Boolean blocked) {
        return companyService.findCustomers(desiredCustomerStartingSubstring, blocked, authentication);
    }

    // TODO: 02/09/2020 test
    @PutMapping("/{companyUid}/customers/{customerId}")
    @ApiOperation("Update companies customer relationship")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Customer updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid) && " +
            "@authenticationPrivilegesService.isCompaniesCustomer(authentication, customerId)")
    public ResourceOperationResponse updateCustomerRelationship(@ApiIgnore AccountAuthentication authentication,
                                                                @ApiIgnore @PathVariable String companyUid,
                                                                @ApiIgnore @PathVariable Long customerId,
                                                                @RequestBody @Valid CompanyCustomerRelationshipManagementRequest companyCustomerManagementRequest) {
        return companyService.updateCustomerRelationship(customerId, companyCustomerManagementRequest, authentication);
    }
}
