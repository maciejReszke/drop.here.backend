package com.drop.here.backend.drophere.authentication.authentication.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.BaseLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ProfileLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationService;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
@Api(tags = "Authentication API")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Logging in")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Logged in", response = LoginResponse.class),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized", response = ExceptionMessage.class)
    })
    public LoginResponse login(@Valid @RequestBody BaseLoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Logging in on profile", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Logged in on profile", response = LoginResponse.class),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized", response = ExceptionMessage.class)
    })
    @PostMapping("/profile")
    public LoginResponse loginOnProfile(@Valid @RequestBody ProfileLoginRequest loginRequest,
                                        @ApiIgnore AccountAuthentication accountAuthentication) {
        return authenticationService.loginOnProfile(loginRequest, accountAuthentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Logout from profile to account", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Logged out from profile, logged on account", response = LoginResponse.class),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized", response = ExceptionMessage.class)
    })
    @DeleteMapping("/profile")
    public LoginResponse logoutFromProfileToAccount(@ApiIgnore AccountAuthentication accountAuthentication) {
        return authenticationService.logoutFromProfileToAccount(accountAuthentication);
    }

    @GetMapping
    @ApiOperation(value = "Authentication info", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Authentication info", response = LoginResponse.class),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized", response = ExceptionMessage.class)
    })
    public AuthenticationResponse getAuthenticationInfo(@ApiIgnore AccountAuthentication accountAuthentication) {
        return authenticationService.getAuthenticationInfo(accountAuthentication);
    }

    @PostMapping("/external")
    @ApiOperation("Login via external authentication provider")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Logged in", response = LoginResponse.class),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public LoginResponse loginWithAuthenticationProvider(@Valid @RequestBody ExternalAuthenticationProviderLoginRequest externalAuthenticationProviderLoginRequest) {
        return authenticationService.loginWithAuthenticationProvider(externalAuthenticationProviderLoginRequest);
    }
}
