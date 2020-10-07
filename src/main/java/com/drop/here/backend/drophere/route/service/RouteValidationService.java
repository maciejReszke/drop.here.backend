package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.route.dto.RouteDropRequest;
import com.drop.here.backend.drophere.route.dto.RouteProductRequest;
import com.drop.here.backend.drophere.route.dto.UnpreparedRouteRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Service
public class RouteValidationService {

    private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HH:mm");

    public void validateCreate(UnpreparedRouteRequest routeRequest) {
        routeRequest.getProducts().forEach(this::validateRouteProductRequest);
        routeRequest.getDrops().forEach(this::validateDropRequest);
    }

    private void validateDropRequest(RouteDropRequest routeDropRequest) {
        final LocalTime dropStartTime = Try.ofSupplier(() -> LocalTime.parse(routeDropRequest.getStartTime(), TIME_PATTERN))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                        "Invalid drop start time %s", routeDropRequest.getStartTime()),
                        RestExceptionStatusCode.DROP_INVALID_DATE));

        final LocalTime dropEndTime = Try.ofSupplier(() -> LocalTime.parse(routeDropRequest.getEndTime(), TIME_PATTERN))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                        "Invalid drop end time %s", routeDropRequest.getEndTime()),
                        RestExceptionStatusCode.DROP_INVALID_DATE));

        if (!dropStartTime.isBefore(dropEndTime)) {
            throw new RestIllegalRequestValueException(String.format(
                    "Invalid drop start %s and end time %s chronology", routeDropRequest.getStartTime(), routeDropRequest.getEndTime()),
                    RestExceptionStatusCode.DROP_INVALID_DATE);
        }
    }

    private void validateRouteProductRequest(RouteProductRequest routeProductRequest) {
        if (routeProductRequest.isLimitedAmount() && routeProductRequest.getAmount() == null) {
            throw new RestIllegalRequestValueException("Having limited amount product must have set amount",
                    RestExceptionStatusCode.ROUTE_PRODUCT_INVALID_AMOUNT);
        }
    }

    public void validateDelete(Route route) {
        if (route.getStatus() != RouteStatus.UNPREPARED) {
            throw new RestIllegalRequestValueException(String.format(
                    "To delete route %s status cannot be %s", route.getId(), route.getStatus()),
                    RestExceptionStatusCode.INVALID_ROUTE_STATUS_DURING_DELETE);
        }
    }

    public void validateUpdateUnprepared(UnpreparedRouteRequest routeRequest, Route route) {
        validateCreate(routeRequest);
        if (route.getStatus() != RouteStatus.UNPREPARED) {
            throw new RestIllegalRequestValueException(String.format(
                    "To update route %s status cannot be %s", route.getId(), route.getStatus()),
                    RestExceptionStatusCode.INVALID_ROUTE_STATUS_DURING_UPDATE);
        }
    }

    public void validatePreparedUpdate(Route route) {
        validateUpdatePresentStatus(route, EnumSet.of(RouteStatus.UNPREPARED), RouteStatus.PREPARED);
    }

    public void validateOngoingUpdate(Route route) {
        validateUpdatePresentStatus(route, EnumSet.of(RouteStatus.PREPARED), RouteStatus.ONGOING);
    }

    public void validateCancelUpdate(Route route) {
        validateUpdatePresentStatus(route, EnumSet.of(RouteStatus.PREPARED, RouteStatus.ONGOING), RouteStatus.CANCELLED);
    }

    private void validateUpdatePresentStatus(Route route, EnumSet<RouteStatus> desiredStatuses, RouteStatus updateToStatus) {
        if (!desiredStatuses.contains(route.getStatus())) {
            throw new RestIllegalRequestValueException(String.format(
                    "In order to change route status to %s it must be in %s but was %s", updateToStatus, desiredStatuses
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(",")), route.getStatus()),
                    RestExceptionStatusCode.ROUTE_STATE_UPDATE_INVALID_STATUS);
        }
    }

}
