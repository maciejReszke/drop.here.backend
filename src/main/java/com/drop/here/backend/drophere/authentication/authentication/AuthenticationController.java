package com.drop.here.backend.drophere.authentication.authentication;

// TODO: 02/08/2020

import com.drop.here.backend.drophere.authentication.account.service.BaseLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    // TODO: 03/08/2020 test, implement, docs
    // TODO: 03/08/2020 wybieranie profilu
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody BaseLoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }
}
