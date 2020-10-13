package com.drop.here.backend.drophere.order.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.order.dto.OrderCustomerDecisionRequest;
import com.drop.here.backend.drophere.order.dto.OrderCustomerResponse;
import com.drop.here.backend.drophere.order.dto.OrderSubmissionRequest;
import com.drop.here.backend.drophere.order.service.OrderService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
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
public class OrderUserController {
    private final OrderService orderService;

    // TODO: 12/10/2020 test (pamietac zeby dac endpoint na konkretne zamowienie i zwracac ile rzeczeywiscie jest w bazie)
    // TODO: 13/10/2020 security
    @GetMapping("/orders")
    @ApiOperation(value = "Find customer orders", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found orders"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Page<OrderCustomerResponse> findCustomerOrders(@ApiIgnore AccountAuthentication authentication,
                                                          @ApiParam(value = "Order status") @RequestParam(required = false) String status,
                                                          Pageable pageable) {
        return orderService.findCustomerOrders(authentication, status, pageable);
    }

    // TODO: 12/10/2020 test
    @ApiOperation(value = "Creating order", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PostMapping("/companies/{companyUid}/drops/{dropUid}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Order created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class),
            @ApiResponse(code = 666, message = "Not enough products", response = ExceptionMessage.class),
    })
    public ResourceOperationResponse createOrder(@ApiIgnore AccountAuthentication authentication,
                                                 @ApiIgnore @PathVariable String dropUid,
                                                 @RequestBody @Valid OrderSubmissionRequest orderSubmissionRequest) {
        return orderService.createOrder(dropUid, orderSubmissionRequest, authentication);
    }

    // TODO: 12/10/2020 test
    @ApiOperation(value = "Updating order (items in order)", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PutMapping("/companies/{companyUid}/drops/{dropUid}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Order updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class),
            @ApiResponse(code = 666, message = "Not enough products", response = ExceptionMessage.class),
    })
    public ResourceOperationResponse updateOrder(@ApiIgnore AccountAuthentication authentication,
                                                 @ApiIgnore Long orderId,
                                                 @RequestBody @Valid OrderSubmissionRequest orderSubmissionRequest) {
        return orderService.updateOrder(orderId, orderSubmissionRequest, authentication);
    }

    // TODO: 13/10/2020 test
    // TODO: 12/10/2020 test, implement + endpointy dla firmy jeszcze trzeba zrobic, info do dropa czy automatycznie zaakceptowany bedzie order
    @ApiOperation(value = "Updating order (status) - customer decisions", authorizations = @Authorization(value = "AUTHORIZATION"))
    @PatchMapping("/companies/{companyUid}/drops/{dropUid}/orders/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Order updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class),
            @ApiResponse(code = 666, message = "Not enough products", response = ExceptionMessage.class),
    })
    public ResourceOperationResponse updateOrderStatus(@ApiIgnore AccountAuthentication authentication,
                                                       @ApiIgnore Long orderId,
                                                       @RequestBody @Valid OrderCustomerDecisionRequest orderCustomerDecisionRequest) {
        return orderService.updateOrderStatus(orderId, orderCustomerDecisionRequest, authentication);
    }


}
