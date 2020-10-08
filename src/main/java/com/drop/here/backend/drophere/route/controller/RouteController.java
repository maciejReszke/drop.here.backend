package com.drop.here.backend.drophere.route.controller;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.dto.RouteShortResponse;
import com.drop.here.backend.drophere.route.dto.RouteStateChangeRequest;
import com.drop.here.backend.drophere.route.dto.UnpreparedRouteRequest;
import com.drop.here.backend.drophere.route.service.RouteService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/companies/{companyUid}/routes")
@Api(tags = "Route API")
public class RouteController {
    private final RouteService routeService;

    @ApiOperation(value = "Find routes", authorizations = @Authorization(value = "AUTHORIZATION"))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of routes"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Page<RouteShortResponse> findRoutes(@ApiIgnore @PathVariable String companyUid,
                                               @ApiIgnore AccountAuthentication accountAuthentication,
                                               @ApiParam(value = "Route status") @RequestParam(required = false) String routeStatus,
                                               Pageable pageable) {
        return routeService.findRoutes(accountAuthentication, routeStatus, pageable);
    }

    @GetMapping("/{routeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found route"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation(value = "Find route", authorizations = @Authorization(value = "AUTHORIZATION"))
    public RouteResponse findRoute(@ApiIgnore @PathVariable String companyUid,
                                   @ApiIgnore @PathVariable Long routeId,
                                   @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.findRoute(routeId, accountAuthentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Creating new route", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Route created"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse createRoute(@ApiIgnore @PathVariable String companyUid,
                                                 @RequestBody @Valid UnpreparedRouteRequest routeRequest,
                                                 @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.createRoute(companyUid, routeRequest, accountAuthentication);
    }

    @PutMapping("/{routeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Route updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation(value = "Updating route", authorizations = @Authorization(value = "AUTHORIZATION"))
    public ResourceOperationResponse updateRoute(@ApiIgnore @PathVariable String companyUid,
                                                 @ApiIgnore @PathVariable Long routeId,
                                                 @RequestBody @Valid UnpreparedRouteRequest routeRequest,
                                                 @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.updateUnpreparedRoute(companyUid, routeId, routeRequest, accountAuthentication);
    }

    @PatchMapping("/{routeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid) && " +
            "hasAuthority('" + PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY + "')")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Route updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation(value = "Updating route", authorizations = @Authorization(value = "AUTHORIZATION"))
    public ResourceOperationResponse updateRoute(@ApiIgnore @PathVariable String companyUid,
                                                 @ApiIgnore @PathVariable Long routeId,
                                                 @RequestBody @Valid RouteStateChangeRequest routeRequest,
                                                 @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.updateRouteStatus(companyUid, routeId, routeRequest, accountAuthentication);
    }


    @DeleteMapping("/{routeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Route deleted"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation(value = "Deleting route", authorizations = @Authorization(value = "AUTHORIZATION"))
    public ResourceOperationResponse deleteRoute(@ApiIgnore @PathVariable String companyUid,
                                                 @ApiIgnore @PathVariable Long routeId,
                                                 @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.deleteRoute(companyUid, routeId, accountAuthentication);
    }
}
