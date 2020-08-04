package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.account.service.BaseLoginRequest;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
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

    @GetMapping
    @ApiOperation("Authentication info")
    @ApiAuthorizationToken
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Authentication info", response = LoginResponse.class),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = "Unauthorized", response = ExceptionMessage.class)
    })
    public AuthenticationResponse getAuthenticationInfo(@ApiIgnore AccountAuthentication accountAuthentication) {
        return authenticationService.getAuthenticationInfo(accountAuthentication);
    }
}
