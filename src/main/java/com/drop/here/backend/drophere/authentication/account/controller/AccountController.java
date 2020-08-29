package com.drop.here.backend.drophere.authentication.account.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInfoResponse;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
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
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Api(tags = "Account management API")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating new account")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Created account", response = LoginResponse.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public LoginResponse createAccount(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        return accountService.createAccount(accountCreationRequest);
    }

    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Fetching account information")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Account info", response = AccountInfoResponse.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnAccountOperation(authentication, #accountId)")
    @ApiAuthorizationToken
    public AccountInfoResponse getAccountInfo(@ApiIgnore @PathVariable Long accountId,
                                              @ApiIgnore AccountAuthentication accountAuthentication) {
        return accountService.getAccountInfo(accountAuthentication);
    }
}
