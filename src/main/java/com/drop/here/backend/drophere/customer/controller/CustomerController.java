package com.drop.here.backend.drophere.customer.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.customer.service.CustomerService;
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
@RequestMapping("/customers")
@Api(tags = "Customer API")
public class CustomerController {
    private final CustomerService customerService;

    // TODO: 01/09/2020 tutaj jeszcze get jezeli maja z ta firma membership
    @GetMapping("/{customerId}/images")
    @ApiOperation("Get customer image")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Customer image"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCustomerOperation(authentication, #customerId)")
    public ResponseEntity<byte[]> findImage(@ApiIgnore AccountAuthentication authentication,
                                            @ApiIgnore @PathVariable Long customerId) {
        final Image image = customerService.findImage(customerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .eTag(customerId + "" + image.getId())
                .contentType(MediaType.IMAGE_JPEG)
                .body(image.getBytes());
    }
}
