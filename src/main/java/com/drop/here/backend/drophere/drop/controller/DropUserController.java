package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.service.DropUserService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drops")
@Api(tags = "Drops user API")
public class DropUserController {
    private final DropUserService dropUserService;

    // TODO: 28/08/2020 test
    @ApiOperation("Listing user's joined (requested) drops")
    @GetMapping("/drops/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "List of drops that user is part of (or created join request)", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse findMemberships(@ApiIgnore AccountAuthentication authentication,
                                                     @ApiParam(value = "Name of drop (prefix)") @RequestParam(required = false) String name,
                                                     @NotNull Pageable pageable) {
        return dropUserService.findMemberships(authentication, name, pageable);
    }

    // TODO: 28/08/2020 test
    @ApiOperation("Joining to drop")
    @PostMapping("/{dropUid}/companies/{companyUid}/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Joined to drop", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse createDropMembership(@ApiIgnore AccountAuthentication authentication,
                                                          @ApiIgnore @PathVariable String dropUid,
                                                          @ApiIgnore @PathVariable String companyUid,
                                                          @RequestBody @Valid DropJoinRequest dropJoinRequest) {
        return dropUserService.createDropMembership(dropJoinRequest, dropUid, companyUid, authentication);
    }

    @ApiOperation("Leaving drop")
    @DeleteMapping("/{dropUid}/companies/{companyUid}/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Left from drop", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse deleteDrop(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String dropUid,
                                                @ApiIgnore @PathVariable String companyUid) {
        return dropUserService.deleteDrop(dropUid, companyUid, authentication);
    }
}
