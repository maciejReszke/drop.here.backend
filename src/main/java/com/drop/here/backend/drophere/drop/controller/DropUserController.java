package com.drop.here.backend.drophere.drop.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.drop.dto.DropDetailedCustomerResponse;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/drops")
@RequiredArgsConstructor
@Api(tags = "Drops user API")
public class DropUserController {
    private final DropService dropService;

    @ApiOperation("Drop details")
    @GetMapping("/{dropUid}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Drop details"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public DropDetailedCustomerResponse findDrop(@ApiIgnore AccountAuthentication authentication,
                                                 @ApiIgnore @PathVariable String dropUid) {
        return dropService.findDrop(dropUid, authentication);
    }

}
