package com.drop.here.backend.drophere.authentication.account.controller;


import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/accounts/{accountId}/profiles")
@RequiredArgsConstructor
@Api(tags = "Account profile management API")
public class AccountProfileController {
    private final AccountProfileService accountProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating new account profile")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Created account profile", response = LoginResponse.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnAccountOperation(authentication, #accountId)")
    @ApiAuthorizationToken
    public LoginResponse createAccountProfile(@Valid @RequestBody AccountProfileCreationRequest accountCreationRequest,
                                              @ApiIgnore @PathVariable Long accountId,
                                              @ApiIgnore AccountAuthentication accountAuthentication) {
        return accountProfileService.createAccountProfile(accountCreationRequest, accountAuthentication);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Updating account profile")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Updated account profile"),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnAccountOperation(authentication, #accountId) && " +
            "@authenticationPrivilegesService.isOwnProfileOperation(authentication, #accountProfileUid)")
    @PatchMapping("/{accountProfileUid}")
    @ApiAuthorizationToken
    public void updateAccountProfile(@Valid @RequestBody AccountProfileUpdateRequest accountCreationRequest,
                                     @ApiIgnore @PathVariable Long accountId,
                                     @ApiIgnore @PathVariable String accountProfileUid,
                                     @ApiIgnore AccountAuthentication accountAuthentication) {
        accountProfileService.updateAccountProfile(accountCreationRequest, accountAuthentication);
    }
}
