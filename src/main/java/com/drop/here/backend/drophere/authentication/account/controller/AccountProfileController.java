package com.drop.here.backend.drophere.authentication.account.controller;


import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts/profiles")
@RequiredArgsConstructor
@Api(tags = "Account profile API")
public class AccountProfileController {
    private final AccountProfileService accountProfileService;

    private static final String IMAGE_PART_NAME = "image";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating new account profile")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created account profile", response = LoginResponse.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiAuthorizationToken
    public Mono<LoginResponse> createAccountProfile(@Valid @RequestBody Mono<AccountProfileCreationRequest> accountProfileCreationRequestMono,
                                                    @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return accountProfileCreationRequestMono.zipWith(accountAuthenticationMono)
                .flatMap(tuple -> accountProfileService.createAccountProfile(tuple.getT1(), tuple.getT2()));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Updating account profile")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Updated account profile"),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PatchMapping
    @ApiAuthorizationToken
    public Mono<Void> updateAccountProfile(@Valid @RequestBody Mono<AccountProfileUpdateRequest> accountProfileUpdateRequestMono,
                                           @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return accountProfileUpdateRequestMono.zipWith(accountAuthenticationMono)
                .flatMap(tuple -> accountProfileService.updateAccountProfile(tuple.getT1(), tuple.getT2()));
    }

    @PostMapping("/images")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update account profile image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> updateImage(@ApiIgnore Mono<AccountAuthentication> authenticationMono,
                                                       @RequestPart(name = IMAGE_PART_NAME) Mono<FilePart> monoImage) {
        return authenticationMono.zipWith(monoImage)
                .flatMap(tuple -> accountProfileService.updateImage(tuple.getT2(), tuple.getT1()));
    }

    @GetMapping("/{profileUid}/images")
    @ApiOperation("Get account profile image")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account profile image"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResponseEntity<byte[]>> findImage(@ApiIgnore @PathVariable String profileUid) {
        return accountProfileService.findImage(profileUid)
                .map(image -> ResponseEntity
                        .status(HttpStatus.OK)
                        .eTag(profileUid + image.getId() + image.getVersion())
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(image.getBytes()));
    }
}
