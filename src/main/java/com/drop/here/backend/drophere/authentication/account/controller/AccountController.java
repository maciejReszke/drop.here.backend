package com.drop.here.backend.drophere.authentication.account.controller;

import com.drop.here.backend.drophere.authentication.account.dto.AccountCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountInformationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.authentication.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    // TODO: 02/08/2020 docs + description 4 gang
    // TODO: 03/08/2020 + profile
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse createAccount(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        return accountService.createAccount(accountCreationRequest);
    }

    // TODO: 02/08/2020 test + docs (after spring security addon)
    @GetMapping
    public AccountInformationResponse getAccountInformation(Account account) {
        return accountService.getAccountInformation(account);
    }
}
