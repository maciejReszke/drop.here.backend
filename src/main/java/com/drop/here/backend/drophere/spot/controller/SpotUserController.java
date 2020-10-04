package com.drop.here.backend.drophere.spot.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotBaseCustomerResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotDetailedCustomerResponse;
import com.drop.here.backend.drophere.spot.service.SpotMembershipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/spots")
@Api(tags = "Spots user API")
public class SpotUserController {
    private final SpotMembershipService spotMembershipService;

    @ApiOperation(value = "Listing all spots", authorizations = @Authorization(value = "AUTHORIZATION"))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "All filtered out spots"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public List<SpotBaseCustomerResponse> findSpots(@ApiIgnore AccountAuthentication authentication,
                                                    @ApiParam(value = "Searching x coordinate", required = true) @RequestParam Double xCoordinate,
                                                    @ApiParam(value = "Searching y coordinate", required = true) @RequestParam Double yCoordinate,
                                                    @ApiParam(value = "Searching radius (meters)", required = true) @RequestParam Integer radius,
                                                    @ApiParam(value = "Is/is not a member") @RequestParam(required = false) Boolean member,
                                                    @ApiParam(value = "Name of spot/company (prefix)", required = true) @RequestParam String namePrefix,
                                                    Pageable pageable) {
        return spotMembershipService.findSpots(authentication, xCoordinate, yCoordinate, radius, member, namePrefix, pageable);
    }

    @ApiOperation(value = "Spot details", authorizations = @Authorization(value = "AUTHORIZATION"))
    @GetMapping("/{spotUid}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Spot details"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public SpotDetailedCustomerResponse findSpot(@ApiIgnore AccountAuthentication authentication,
                                                 @ApiIgnore @PathVariable String spotUid) {
        return spotMembershipService.findSpot(spotUid, authentication);
    }

    @ApiOperation(value = "Joining to spot", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PostMapping("/{spotUid}/companies/{companyUid}/memberships")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Joined to drop", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isCompanyVisibleForCustomer(authentication, #companyUid)")
    public ResourceOperationResponse createSpotMembership(@ApiIgnore AccountAuthentication authentication,
                                                          @ApiIgnore @PathVariable String spotUid,
                                                          @ApiIgnore @PathVariable String companyUid,
                                                          @RequestBody @Valid SpotJoinRequest spotJoinRequest) {
        return spotMembershipService.createSpotMembership(spotJoinRequest, spotUid, companyUid, authentication);
    }

    @ApiOperation(value = "Get spot by memberships info", authorizations = @Authorization(value = "AUTHORIZATION"))
    @GetMapping("/memberships")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of spots that is member or is pending"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public List<SpotBaseCustomerResponse> findSpotsByMemberships(@ApiIgnore AccountAuthentication authentication) {
        return spotMembershipService.findSpotsByMemberships(authentication);
    }

    @ApiOperation(value = "Updating spot membership", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PutMapping("/{spotUid}/companies/{companyUid}/memberships")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Membership updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse updateSpotMembership(@ApiIgnore AccountAuthentication authentication,
                                                          @ApiIgnore @PathVariable String spotUid,
                                                          @ApiIgnore @PathVariable String companyUid,
                                                          @RequestBody @Valid SpotMembershipManagementRequest spotMembershipManagementRequest) {
        return spotMembershipService.updateSpotMembership(spotMembershipManagementRequest, spotUid, companyUid, authentication);
    }


    @ApiOperation(value = "Leaving drop", authorizations = @Authorization(value = "AUTHORIZATION"))
    @DeleteMapping("/{spotUid}/companies/{companyUid}/memberships")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Left from drop", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse deleteSpotMembership(@ApiIgnore AccountAuthentication authentication,
                                                          @ApiIgnore @PathVariable String spotUid,
                                                          @ApiIgnore @PathVariable String companyUid) {
        return spotMembershipService.deleteSpotMembership(spotUid, companyUid, authentication);
    }
}
