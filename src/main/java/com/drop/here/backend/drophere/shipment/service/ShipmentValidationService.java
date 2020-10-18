package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.drop.service.DropValidationService;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentValidationService {
    private final DropValidationService dropValidationService;

    public void validateShipment(Shipment shipment) {
        dropValidationService.validateDropForShipment(shipment.getDrop());
        shipment.getProducts().forEach(this::validateShipmentProduct);
    }

    private void validateShipmentProduct(ShipmentProduct product) {
        validateRequiredCustomizations(product);
        validateUniqueCustomizations(product);
        validateCustomizationWrapperType(product);
        validateQuantity(product);
    }

    private void validateQuantity(ShipmentProduct product) {
        final BigDecimal quantity = product.getQuantity();
        final BigDecimal unitFraction = product.getProduct().getUnitFraction();
        if (tryDivideToInteger(unitFraction, quantity).isFailure()) {
            throw new RestIllegalRequestValueException(String.format(
                    "To order product %s quantity %s must be dividable by %s",
                    product.getProduct().getId(),
                    quantity,
                    unitFraction),
                    RestExceptionStatusCode.INVALID_SHIPMENT_PRODUCT_QUANTITY_NOT_DIVIDABLE_BY_UNIT_FRACTION);
        }
    }

    private Try<BigDecimal> tryDivideToInteger(BigDecimal unitFraction, BigDecimal quantity) {
        return Try.ofSupplier(() -> quantity.divide(unitFraction, RoundingMode.UNNECESSARY));
    }

    private void validateCustomizationWrapperType(ShipmentProduct product) {
        product.getCustomizations().stream()
                .map(ShipmentProductCustomization::getProductCustomization)
                .collect(Collectors.groupingBy(customization -> customization.getWrapper().getId()))
                .forEach((customizationWrapperId, productCustomizations) -> validateCustomizationWrapperType(productCustomizations));
    }

    private void validateCustomizationWrapperType(List<ProductCustomization> productCustomizations) {
        final ProductCustomizationWrapper wrapper = productCustomizations.get(0).getWrapper();
        if (wrapper.getType() == ProductCustomizationWrapperType.SINGLE && productCustomizations.size() > 1) {
            throw new RestIllegalRequestValueException(String.format(
                    "To order product %s with customization wrapper %s only one customization can be picked because it is of type SINGLE", wrapper.getProduct().getId(), wrapper.getId()),
                    RestExceptionStatusCode.INVALID_SHIPMENT_MULTIPLE_CUSTOMIZATIONS_FOR_SINGLE_CUSTOMIZATION_WRAPPER_TYPE);
        }
    }

    private void validateUniqueCustomizations(ShipmentProduct product) {
        final List<Long> customizationsIds = product.getCustomizations().stream()
                .map(ShipmentProductCustomization::getProductCustomization)
                .map(ProductCustomization::getId)
                .collect(Collectors.toList());

        final long uniqueCustomizations = new HashSet<>(customizationsIds).size();

        if (customizationsIds.size() != uniqueCustomizations) {
            throw new RestIllegalRequestValueException(String.format(
                    "To order product %s customizations must be unique", product.getRouteProduct().getProduct().getId()),
                    RestExceptionStatusCode.INVALID_SHIPMENT_NON_UNIQUE_CUSTOMIZATIONS);
        }
    }

    private void validateRequiredCustomizations(ShipmentProduct product) {
        final Set<Long> requiredWrappersIds = product.getRouteProduct()
                .getProduct()
                .getCustomizationWrappers()
                .stream()
                .filter(ProductCustomizationWrapper::isRequired)
                .map(ProductCustomizationWrapper::getId)
                .collect(Collectors.toSet());

        final Set<Long> actualWrappersIds = product.getCustomizations().stream()
                .map(ShipmentProductCustomization::getProductCustomization)
                .map(ProductCustomization::getWrapper)
                .map(ProductCustomizationWrapper::getId)
                .collect(Collectors.toSet());

        if (!actualWrappersIds.containsAll(requiredWrappersIds)) {
            throw new RestIllegalRequestValueException(String.format(
                    "To order product %s all required wrappers must be included", product.getRouteProduct().getProduct().getId()),
                    RestExceptionStatusCode.INVALID_SHIPMENT_PRODUCT_WITHOUT_REQUIRED_CUSTOMIZATIONS);
        }
    }

    public void validateShipmentCustomerUpdate(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.PLACED),
                ShipmentStatus.PLACED
        );
    }

    public void validateCancelCustomerDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.ACCEPTED, ShipmentStatus.PLACED, ShipmentStatus.COMPROMISED),
                ShipmentStatus.CANCELLED
        );
    }

    public void validateAcceptCustomerDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.COMPROMISED),
                ShipmentStatus.ACCEPTED
        );
    }

    public void validateCancelCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.CANCEL_REQUESTED),
                ShipmentStatus.CANCELLED
        );
    }

    public void validateAcceptCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.PLACED, ShipmentStatus.DELIVERED),
                ShipmentStatus.ACCEPTED
        );
    }

    public void validateDeliverCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.ACCEPTED, ShipmentStatus.CANCEL_REQUESTED),
                ShipmentStatus.DELIVERED
        );
    }

    public void validateRejectCompanyDecision(Shipment shipment) {
        validateUpdateCurrentStatus(
                shipment,
                EnumSet.of(ShipmentStatus.COMPROMISED, ShipmentStatus.PLACED, ShipmentStatus.ACCEPTED),
                ShipmentStatus.REJECTED
        );
    }

    private void validateUpdateCurrentStatus(Shipment shipment, EnumSet<ShipmentStatus> desiredStatuses, ShipmentStatus updateToStatus) {
        if (!desiredStatuses.contains(shipment.getStatus())) {
            throw new RestIllegalRequestValueException(String.format(
                    "In order to change shipment status to %s it must be in %s but was %s", updateToStatus, desiredStatuses
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(",")), shipment.getStatus()),
                    RestExceptionStatusCode.SHIPMENT_UPDATE_INVALID_STATUS);
        }
    }
}
