package com.drop.here.backend.drophere.authentication.account.controller;


import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileCreationRequest;
import com.drop.here.backend.drophere.authentication.account.dto.AccountProfileUpdateRequest;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
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
    @ApiOperation(value = "Creating new account profile", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Created account profile", response = LoginResponse.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public LoginResponse createAccountProfile(@Valid @RequestBody AccountProfileCreationRequest accountCreationRequest,
                                              @ApiIgnore AccountAuthentication accountAuthentication) {
        return accountProfileService.createAccountProfile(accountCreationRequest, accountAuthentication);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Updating account profile", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Updated account profile"),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PatchMapping
    public void updateAccountProfile(@Valid @RequestBody AccountProfileUpdateRequest accountCreationRequest,
                                     @ApiIgnore AccountAuthentication accountAuthentication) {
        accountProfileService.updateAccountProfile(accountCreationRequest, accountAuthentication);
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update account profile image", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Image updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse updateImage(@ApiIgnore AccountAuthentication authentication,
                                                 @RequestPart(name = IMAGE_PART_NAME) MultipartFile image) {
        return accountProfileService.updateImage(image, authentication);
    }

    @GetMapping("/{profileUid}/images")
    @ApiOperation("Get account profile image")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Account profile image"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResponseEntity<byte[]> findImage(@ApiIgnore @PathVariable String profileUid) {
        final Image image = accountProfileService.findImage(profileUid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .eTag(profileUid + image.getId())
                .contentType(MediaType.IMAGE_JPEG)
                .body(image.getBytes());
    }
}
