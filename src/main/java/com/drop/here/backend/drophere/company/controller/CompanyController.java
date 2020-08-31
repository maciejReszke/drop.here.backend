package com.drop.here.backend.drophere.company.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.company.service.CompanyService;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
@Api(tags = "Company customer API")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/{companyUid}/images")
    @ApiOperation("Get company image")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Company image"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid) or " +
            "@authenticationPrivilegesService.isCompanyVisible(#companyUid)")
    public ResponseEntity<byte[]> findImage(@ApiIgnore AccountAuthentication authentication,
                                            @ApiIgnore @PathVariable String companyUid) {
        final Image image = companyService.findImage(companyUid);
        return ResponseEntity
                .status(HttpStatus.OK)
                .eTag(companyUid + image.getId())
                .contentType(MediaType.IMAGE_JPEG)
                .body(image.getBytes());
    }
}
