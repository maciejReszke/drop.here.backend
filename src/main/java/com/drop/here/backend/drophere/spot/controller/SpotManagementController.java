package com.drop.here.backend.drophere.spot.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.service.SpotManagementService;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies/{companyUid}/spots")
@Api(tags = "Spots management API")
public class SpotManagementController {
    private final SpotManagementService spotManagementService;

    @ApiOperation("Listing companies spots")
    @GetMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of spots"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public List<SpotCompanyResponse> findSpots(@ApiIgnore AccountAuthentication authentication,
                                               @ApiIgnore @PathVariable String companyUid,
                                               @ApiParam(value = "Name of spot (prefix)") @RequestParam(required = false) String name) {
        return spotManagementService.findCompanySpots(companyUid, name);
    }

    @ApiOperation("Creating spot")
    @PostMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Spot created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse createSpot(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String companyUid,
                                                @RequestBody @Valid SpotManagementRequest spotManagementRequest) {
        return spotManagementService.createSpot(spotManagementRequest, companyUid, authentication);
    }

    @ApiOperation("Updating spot")
    @PutMapping("/{spotId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Spot updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse updateSpot(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String companyUid,
                                                @ApiIgnore @PathVariable Long spotId,
                                                @RequestBody @Valid SpotManagementRequest spotManagementRequest) {
        return spotManagementService.updateSpot(spotManagementRequest, spotId, companyUid);
    }

    @ApiOperation("Deleting spot")
    @DeleteMapping("/{spotId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Spot deleted", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse deleteSpot(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String companyUid,
                                                @ApiIgnore @PathVariable Long spotId) {
        return spotManagementService.deleteSpot(spotId, companyUid);
    }

    @ApiOperation("Listing members of given spot")
    @GetMapping("/{spotId}/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Spot memberships listed"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Page<SpotCompanyMembershipResponse> findMemberships(@ApiIgnore AccountAuthentication authentication,
                                                               @ApiIgnore @PathVariable String companyUid,
                                                               @ApiIgnore @PathVariable Long spotId,
                                                               @ApiParam(value = "Customer name (starting with name or starting with surname)") @RequestParam(value = "customerName", required = false) String desiredCustomerStartingSubstring,
                                                               @ApiParam(value = "Membership status") @RequestParam(value = "membershipStatus", required = false) String membershipStatus,
                                                               Pageable pageable) {
        return spotManagementService.findMemberships(spotId, companyUid, desiredCustomerStartingSubstring, membershipStatus, pageable);
    }

    @ApiOperation("Update membership")
    @PutMapping("/{spotId}/memberships/{membershipId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Membership updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse updateMembership(@ApiIgnore AccountAuthentication authentication,
                                                      @ApiIgnore @PathVariable String companyUid,
                                                      @ApiIgnore @PathVariable Long spotId,
                                                      @ApiIgnore @PathVariable Long membershipId,
                                                      @RequestBody @Valid SpotCompanyMembershipManagementRequest spotCompanyMembershipManagementRequest) {
        return spotManagementService.updateMembership(spotId, companyUid, membershipId, spotCompanyMembershipManagementRequest);
    }
}