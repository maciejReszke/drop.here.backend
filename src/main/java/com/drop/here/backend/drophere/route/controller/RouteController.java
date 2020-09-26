package com.drop.here.backend.drophere.route.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.dto.RouteShortResponse;
import com.drop.here.backend.drophere.route.service.RouteService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/companies/{companyUid}/routes")
public class RouteController {
    private final RouteService routeService;

    // TODO: 25/09/2020 test
    @ApiOperation("Find routes")
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
                                               @ApiParam(value = "Customer name (starting with name or starting with surname)") @RequestParam(required = false) String routeStatus,
                                               Pageable pageable) {
        return routeService.findRoutes(accountAuthentication, routeStatus, pageable);
    }

    // TODO: 25/09/2020 test
    @GetMapping("/{routeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found route"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Find route")
    public RouteResponse findRoute(@ApiIgnore @PathVariable String companyUid,
                                   @ApiIgnore @PathVariable Long routeId,
                                   @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.findRoute(routeId, accountAuthentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating new route")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Route created"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse createRoute(@ApiIgnore @PathVariable String companyUid,
                                                 @RequestBody @Valid RouteRequest routeRequest,
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
    @ApiOperation("Updating route")
    public ResourceOperationResponse updateRoute(@ApiIgnore @PathVariable String companyUid,
                                                 @ApiIgnore @PathVariable Long routeId,
                                                 @RequestBody @Valid RouteRequest routeRequest,
                                                 @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.updateRoute(companyUid, routeId, routeRequest, accountAuthentication);
    }

    @DeleteMapping("/{routeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Route deleted"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Deleting route")
    public ResourceOperationResponse deleteRoute(@ApiIgnore @PathVariable String companyUid,
                                                 @ApiIgnore @PathVariable Long routeId,
                                                 @ApiIgnore AccountAuthentication accountAuthentication) {
        return routeService.deleteRoute(companyUid, routeId, accountAuthentication);
    }
}
