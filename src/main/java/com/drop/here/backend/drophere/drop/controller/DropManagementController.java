package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.drop.dto.DropCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyMembershipResponse;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyResponse;
import com.drop.here.backend.drophere.drop.service.DropManagementService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
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
@RequestMapping("/companies/{companyUid}/drops")
@Api(tags = "Drops management API")
public class DropManagementController {
    private final DropManagementService dropManagementService;

    @ApiOperation("Listing companies drops")
    @GetMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of drops", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public List<DropCompanyResponse> findDrops(@ApiIgnore AccountAuthentication authentication,
                                               @ApiIgnore @PathVariable String companyUid,
                                               @ApiParam(value = "Name of drop (prefix)") @RequestParam(required = false) String name) {
        return dropManagementService.findCompanyDrops(companyUid, name);
    }

    @ApiOperation("Creating drop")
    @PostMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Drop created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse createDrop(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String companyUid,
                                                @RequestBody @Valid DropManagementRequest dropManagementRequest) {
        return dropManagementService.createDrop(dropManagementRequest, companyUid, authentication);
    }

    @ApiOperation("Updating drop")
    @PutMapping("/{dropId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Drop updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse updateDrop(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String companyUid,
                                                @ApiIgnore @PathVariable Long dropId,
                                                @RequestBody @Valid DropManagementRequest dropManagementRequest) {
        return dropManagementService.updateDrop(dropManagementRequest, dropId, companyUid);
    }

    @ApiOperation("Deleting drop")
    @DeleteMapping("/{dropId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Drop deleted", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse deleteDrop(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String companyUid,
                                                @ApiIgnore @PathVariable Long dropId) {
        return dropManagementService.deleteDrop(dropId, companyUid);
    }

    // TODO: 01/09/2020 test
    @ApiOperation("Listing members of given drop")
    @GetMapping("/{dropId}/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Drop memberships listed"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Page<DropCompanyMembershipResponse> findMemberships(@ApiIgnore AccountAuthentication authentication,
                                                               @ApiIgnore @PathVariable String companyUid,
                                                               @ApiIgnore @PathVariable Long dropId,
                                                               @ApiParam(value = "Customer name (starting with name or starting with surname)") @RequestParam(value = "customerName", required = false) String desiredCustomerStartingSubstring,
                                                               @ApiParam(value = "Membership status") @RequestParam(value = "membershipStatus", required = false) String membershipStatus,
                                                               Pageable pageable) {
        return dropManagementService.findMemberships(dropId, companyUid, desiredCustomerStartingSubstring, membershipStatus, pageable);
    }

    // TODO: 01/09/2020 test
    @ApiOperation("Update membership")
    @PutMapping("/{dropId}/memberships/{membershipId}")
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
                                                      @ApiIgnore @PathVariable Long dropId,
                                                      @ApiIgnore @PathVariable Long membershipId,
                                                      @RequestBody @Valid DropCompanyMembershipManagementRequest dropCompanyMembershipManagementRequest) {
        return dropManagementService.updateMembership(dropId, companyUid, membershipId, dropCompanyMembershipManagementRequest);
    }
}
