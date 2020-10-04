package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.drop.dto.DropManagementRequest;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/management/companies/drops")
@RequiredArgsConstructor
@Api(tags = "Drops management API")
public class DropManagementController {
    private final DropService dropService;

    // TODO: 04/10/2020 test
    @ApiOperation(value = "Update drop", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PutMapping("/{dropUid}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Drop updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("hasAuthority('" + PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY + "')")
    public ResourceOperationResponse updateDrop(@ApiIgnore AccountAuthentication authentication,
                                                @ApiIgnore @PathVariable String dropUid,
                                                @RequestBody @Valid DropManagementRequest dropManagementRequest) {
        return dropService.updateDrop(dropManagementRequest, dropUid, authentication);
    }
}
