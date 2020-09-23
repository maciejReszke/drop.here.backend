package com.drop.here.backend.drophere.authentication.authentication.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.BaseLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ProfileLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationService;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

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
            @ApiResponse(code = 200, message = "Logged in", response = LoginResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ExceptionMessage.class)
    })
    public Mono<LoginResponse> login(@Valid @RequestBody Mono<BaseLoginRequest> baseLoginRequestMono) {
        return baseLoginRequestMono.flatMap(authenticationService::login);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Logging in on profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Logged in on profile", response = LoginResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ExceptionMessage.class)
    })
    @ApiAuthorizationToken
    @PostMapping("/profile")
    public Mono<LoginResponse> loginOnProfile(@Valid @RequestBody Mono<ProfileLoginRequest> loginRequestMono,
                                              @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return loginRequestMono.zipWith(accountAuthenticationMono)
                .flatMap(tuple -> authenticationService.loginOnProfile(tuple.getT1(), tuple.getT2()));
    }

    @GetMapping
    @ApiOperation("Authentication info")
    @ApiAuthorizationToken
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authentication info", response = LoginResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ExceptionMessage.class)
    })
    public Mono<AuthenticationResponse> getAuthenticationInfo(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return accountAuthenticationMono.flatMap(authenticationService::getAuthenticationInfo);
    }

    @PostMapping("/external")
    @ApiOperation("Login via external authentication provider")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Logged in", response = LoginResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<LoginResponse> loginWithAuthenticationProvider(@Valid @RequestBody Mono<ExternalAuthenticationProviderLoginRequest> externalAuthenticationProviderLoginRequestMono) {
        return externalAuthenticationProviderLoginRequestMono.flatMap(authenticationService::loginWithAuthenticationProvider);
    }
}
