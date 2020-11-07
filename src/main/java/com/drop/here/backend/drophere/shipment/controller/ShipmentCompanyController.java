package com.drop.here.backend.drophere.shipment.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCompanyDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentResponse;
import com.drop.here.backend.drophere.shipment.service.ShipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Api(tags = "Shipment company management API")
public class ShipmentCompanyController {
    private final ShipmentService shipmentService;

    @GetMapping("/companies/{companyUid}/shipments/{shipmentId}")
    @ApiOperation(value = "Find company shipment", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found shipment"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ShipmentResponse findShipment(@ApiIgnore AccountAuthentication authentication,
                                         @ApiIgnore @PathVariable String companyUid,
                                         @ApiIgnore @PathVariable Long shipmentId) {
        return shipmentService.findCompanyShipment(authentication, shipmentId);
    }

    @GetMapping("/companies/{companyUid}/shipments")
    @ApiOperation(value = "Find company shipments", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found shipments"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Page<ShipmentResponse> findCompanyShipments(@ApiIgnore AccountAuthentication authentication,
                                                       @ApiIgnore @PathVariable String companyUid,
                                                       @ApiParam(value = "Shipment status") @RequestParam(required = false) String status,
                                                       @ApiParam(value = "Route id") @RequestParam(required = false) Long routeId,
                                                       @ApiParam(value = "Drop uid") @RequestParam(required = false) String dropUid,
                                                       Pageable pageable) {
        return shipmentService.findCompanyShipments(authentication, status, routeId, dropUid, pageable);
    }

    @ApiOperation(value = "Updating shipment (status) - company decisions", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PatchMapping("/companies/{companyUid}/shipments/{shipmentId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Shipment updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse updateShipmentStatus(@ApiIgnore AccountAuthentication authentication,
                                                          @ApiIgnore @PathVariable String companyUid,
                                                          @ApiIgnore @PathVariable Long shipmentId,
                                                          @RequestBody @Valid ShipmentCompanyDecisionRequest shipmentCompanyDecisionRequest) {
        return shipmentService.updateShipmentStatus(shipmentId, shipmentCompanyDecisionRequest, authentication);
    }
}
