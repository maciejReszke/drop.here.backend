package com.drop.here.backend.drophere.shipment.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerDecisionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentCustomerSubmissionRequest;
import com.drop.here.backend.drophere.shipment.dto.ShipmentResponse;
import com.drop.here.backend.drophere.shipment.service.ShipmentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
public class ShipmentCustomerController {
    private final ShipmentService shipmentService;

    @GetMapping("/shipments/{shipmentId}")
    @ApiOperation(value = "Find customer shipment", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found shipment"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ShipmentResponse findShipment(@ApiIgnore AccountAuthentication authentication,
                                         @ApiIgnore @PathVariable Long shipmentId) {
        return shipmentService.findCustomerShipment(authentication, shipmentId);
    }

    @GetMapping("/shipments")
    @ApiOperation(value = "Find customer shipments", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found shipments"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Page<ShipmentResponse> findCustomerShipments(@ApiIgnore AccountAuthentication authentication,
                                                        @ApiParam(value = "Shipment status") @RequestParam(required = false) String status,
                                                        Pageable pageable) {
        return shipmentService.findCustomerShipments(authentication, status, pageable);
    }

    @ApiOperation(value = "Creating shipments", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PostMapping("/companies/{companyUid}/drops/{dropUid}/shipments")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Shipment created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class),
            @ApiResponse(code = 601, message = "Not enough products to create order", response = ExceptionMessage.class),
    })
    public ResourceOperationResponse createShipment(@ApiIgnore AccountAuthentication authentication,
                                                    @ApiIgnore @PathVariable String dropUid,
                                                    @RequestBody @Valid ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest) {
        return shipmentService.createShipment(dropUid, shipmentCustomerSubmissionRequest, authentication);
    }

    @ApiOperation(value = "Updating shipment (items in shipment)", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PutMapping("/companies/{companyUid}/drops/{dropUid}/shipments/{shipmentId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Shipment updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class),
            @ApiResponse(code = 601, message = "Not enough products", response = ExceptionMessage.class),
    })
    public ResourceOperationResponse updateShipment(@ApiIgnore AccountAuthentication authentication,
                                                    @ApiIgnore @PathVariable Long shipmentId,
                                                    @RequestBody @Valid ShipmentCustomerSubmissionRequest shipmentCustomerSubmissionRequest) {
        return shipmentService.update(shipmentId, shipmentCustomerSubmissionRequest, authentication);
    }

    @ApiOperation(value = "Updating shipment (status) - customer decisions", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PatchMapping("/companies/{companyUid}/drops/{dropUid}/shipments/{shipmentId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Shipment updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse updateShipmentStatus(@ApiIgnore AccountAuthentication authentication,
                                                          @ApiIgnore @PathVariable Long shipmentId,
                                                          @RequestBody @Valid ShipmentCustomerDecisionRequest shipmentCustomerDecisionRequest) {
        return shipmentService.updateShipmentStatus(shipmentId, shipmentCustomerDecisionRequest, authentication);
    }


}
