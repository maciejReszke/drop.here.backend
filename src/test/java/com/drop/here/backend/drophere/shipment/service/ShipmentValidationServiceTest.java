package com.drop.here.backend.drophere.shipment.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.service.DropValidationService;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomization;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.enums.ProductCustomizationWrapperType;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.shipment.entity.Shipment;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProduct;
import com.drop.here.backend.drophere.shipment.entity.ShipmentProductCustomization;
import com.drop.here.backend.drophere.shipment.enums.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ShipmentValidationServiceTest {

    @InjectMocks
    private ShipmentValidationService shipmentValidationService;

    @Mock
    private DropValidationService dropValidationService;

    @Test
    void givenCompromisedShipmentWhenValidateRejectCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenPlacedShipmentWhenValidateRejectCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateRejectCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateRejectCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateRejectCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenAcceptedShipmentWhenValidateDeliverCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateDeliverCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenCancelRequestedShipmentWhenValidateDeliverCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.CANCEL_REQUESTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateDeliverCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateDeliverCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateDeliverCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPlacedShipmentWhenValidateAcceptCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateAcceptCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateAcceptCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenCancelRequestedShipmentWhenValidateCancelCompanyDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.CANCEL_REQUESTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCompanyDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateCancelCompanyDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCompanyDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenCompromisedShipmentWhenValidateAcceptCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateAcceptCustomerDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateAcceptCustomerDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenAcceptedShipmentWhenValidateCancelCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenPlacedShipmentWhenValidateCancelCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenCompromisedShipmentWhenValidateCancelCustomerDecisionThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.COMPROMISED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenDeliveredShipmentWhenValidateCancelCustomerDecisionThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.DELIVERED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateCancelCustomerDecision(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenPlacedShipmentWhenValidateShipmentCustomerUpdateThenDoNothing() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.PLACED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipmentCustomerUpdate(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenAcceptedShipmentWhenValidateCustomerShipmentUpdateThenThrowException() {
        //given
        final Shipment shipment = Shipment.builder().status(ShipmentStatus.ACCEPTED).build();

        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipmentCustomerUpdate(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenValidShipmentWhenValidateShipmentThenDoNothing() {
        //given
        final Drop drop = Drop.builder().build();
        final ProductCustomizationWrapper notRequiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(5L)
                .required(false)
                .build();
        final ProductCustomizationWrapper requiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(6L)
                .required(true)
                .build();
        final Product routeProductProduct = Product.builder()
                .customizationWrappers(List.of(requiredRouteProductProductCustomizationWrapper, notRequiredRouteProductProductCustomizationWrapper))
                .build();
        final RouteProduct routeProduct = RouteProduct.builder()
                .product(routeProductProduct)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper1 = ProductCustomizationWrapper.builder()
                .id(6L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization1 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper1)
                .id(6L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper2 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.MULTIPLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization2 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper2)
                .id(7L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper3 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.MULTIPLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization3 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper3)
                .id(8L)
                .build();
        final ShipmentProductCustomization shipmentProductCustomization1 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization1)
                .price(BigDecimal.valueOf(5)).build();
        final ShipmentProductCustomization shipmentProductCustomization2 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization2)
                .price(BigDecimal.valueOf(6)).build();
        final ShipmentProductCustomization shipmentProductCustomization3 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization3)
                .price(BigDecimal.valueOf(7)).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .routeProduct(routeProduct)
                .customizations(Set.of(shipmentProductCustomization1, shipmentProductCustomization2, shipmentProductCustomization3))
                .quantity(BigDecimal.valueOf(55.22))
                .product(Product.builder().unitFraction(BigDecimal.valueOf(27.61)).build())
                .build();

        final Shipment shipment = Shipment.builder()
                .drop(drop)
                .products(Set.of(shipmentProduct))
                .build();

        doNothing().when(dropValidationService).validateDropForShipment(drop);
        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipment(shipment));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenLackOfRequiredCustomizationsWhenValidateShipmentThenThrowException() {
        //given
        final Drop drop = Drop.builder().build();
        final ProductCustomizationWrapper notRequiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(5L)
                .required(false)
                .build();
        final ProductCustomizationWrapper requiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(6L)
                .required(true)
                .build();
        final Product routeProductProduct = Product.builder()
                .customizationWrappers(List.of(requiredRouteProductProductCustomizationWrapper, notRequiredRouteProductProductCustomizationWrapper))
                .build();
        final RouteProduct routeProduct = RouteProduct.builder()
                .product(routeProductProduct)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper1 = ProductCustomizationWrapper.builder()
                .id(8L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization1 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper1)
                .id(6L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper2 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.MULTIPLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization2 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper2)
                .id(7L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper3 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.MULTIPLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization3 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper3)
                .id(8L)
                .build();
        final ShipmentProductCustomization shipmentProductCustomization1 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization1)
                .price(BigDecimal.valueOf(7)).build();
        final ShipmentProductCustomization shipmentProductCustomization2 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization2)
                .price(BigDecimal.valueOf(6)).build();
        final ShipmentProductCustomization shipmentProductCustomization3 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization3)
                .price(BigDecimal.valueOf(5)).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .routeProduct(routeProduct)
                .customizations(Set.of(shipmentProductCustomization1, shipmentProductCustomization2, shipmentProductCustomization3))
                .quantity(BigDecimal.valueOf(55.22))
                .product(Product.builder().unitFraction(BigDecimal.valueOf(27.61)).build())
                .build();

        final Shipment shipment = Shipment.builder()
                .drop(drop)
                .products(Set.of(shipmentProduct))
                .build();

        doNothing().when(dropValidationService).validateDropForShipment(drop);
        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipment(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) throwable).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_SHIPMENT_PRODUCT_WITHOUT_REQUIRED_CUSTOMIZATIONS.ordinal());
    }

    @Test
    void givenNonUniqueCustomizationsWhenValidateShipmentThenThrowException() {
        //given
        final Drop drop = Drop.builder().build();
        final ProductCustomizationWrapper notRequiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(5L)
                .required(false)
                .build();
        final ProductCustomizationWrapper requiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(6L)
                .required(true)
                .build();
        final Product routeProductProduct = Product.builder()
                .customizationWrappers(List.of(requiredRouteProductProductCustomizationWrapper, notRequiredRouteProductProductCustomizationWrapper))
                .build();
        final RouteProduct routeProduct = RouteProduct.builder()
                .product(routeProductProduct)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper1 = ProductCustomizationWrapper.builder()
                .id(6L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization1 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper1)
                .id(6L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper2 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.MULTIPLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization2 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper2)
                .id(7L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper3 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization3 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper3)
                .id(7L)
                .price(BigDecimal.ONE)
                .build();
        final ShipmentProductCustomization shipmentProductCustomization1 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization1)
                .price(BigDecimal.valueOf(5)).build();
        final ShipmentProductCustomization shipmentProductCustomization2 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization2)
                .price(BigDecimal.valueOf(6)).build();
        final ShipmentProductCustomization shipmentProductCustomization3 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization3)
                .price(BigDecimal.valueOf(7)).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .routeProduct(routeProduct)
                .customizations(Set.of(shipmentProductCustomization1, shipmentProductCustomization2, shipmentProductCustomization3))
                .quantity(BigDecimal.valueOf(55.22))
                .product(Product.builder().unitFraction(BigDecimal.valueOf(27.61)).build())
                .build();

        final Shipment shipment = Shipment.builder()
                .drop(drop)
                .products(Set.of(shipmentProduct))
                .build();

        doNothing().when(dropValidationService).validateDropForShipment(drop);
        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipment(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) throwable).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_SHIPMENT_NON_UNIQUE_CUSTOMIZATIONS.ordinal());
    }

    @Test
    void givenNotSingleCustomizationForCustomizationWrapperOfTypeSingleWhenValidateShipmentThenThrowException() {
        //given
        final Drop drop = Drop.builder().build();
        final ProductCustomizationWrapper notRequiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(5L)
                .required(false)
                .product(Product.builder().build())
                .build();
        final ProductCustomizationWrapper requiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(6L)
                .required(true)
                .product(Product.builder().build())
                .build();
        final Product routeProductProduct = Product.builder()
                .customizationWrappers(List.of(requiredRouteProductProductCustomizationWrapper, notRequiredRouteProductProductCustomizationWrapper))
                .build();
        final RouteProduct routeProduct = RouteProduct.builder()
                .product(routeProductProduct)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper1 = ProductCustomizationWrapper.builder()
                .id(6L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization1 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper1)
                .id(6L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper2 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .product(Product.builder().build())
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization2 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper2)
                .id(7L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper3 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .product(Product.builder().build())
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization3 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper3)
                .id(8L)
                .build();
        final ShipmentProductCustomization shipmentProductCustomization1 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization1)
                .price(BigDecimal.valueOf(5)).build();
        final ShipmentProductCustomization shipmentProductCustomization2 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization2)
                .price(BigDecimal.valueOf(6)).build();
        final ShipmentProductCustomization shipmentProductCustomization3 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization3)
                .price(BigDecimal.valueOf(7)).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .routeProduct(routeProduct)
                .customizations(Set.of(shipmentProductCustomization1, shipmentProductCustomization2, shipmentProductCustomization3))
                .quantity(BigDecimal.valueOf(55.22))
                .product(Product.builder().unitFraction(BigDecimal.valueOf(27.61)).build())
                .build();

        final Shipment shipment = Shipment.builder()
                .drop(drop)
                .products(Set.of(shipmentProduct))
                .build();

        doNothing().when(dropValidationService).validateDropForShipment(drop);
        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipment(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) throwable).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_SHIPMENT_MULTIPLE_CUSTOMIZATIONS_FOR_SINGLE_CUSTOMIZATION_WRAPPER_TYPE.ordinal());
    }

    @Test
    void givenQuantityNotMultiplicityOfUnitFractionWhenValidateShipmentThenThrowException() {
        //given
        final Drop drop = Drop.builder().build();
        final ProductCustomizationWrapper notRequiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(5L)
                .required(false)
                .build();
        final ProductCustomizationWrapper requiredRouteProductProductCustomizationWrapper = ProductCustomizationWrapper.builder()
                .id(6L)
                .required(true)
                .build();
        final Product routeProductProduct = Product.builder()
                .customizationWrappers(List.of(requiredRouteProductProductCustomizationWrapper, notRequiredRouteProductProductCustomizationWrapper))
                .build();
        final RouteProduct routeProduct = RouteProduct.builder()
                .product(routeProductProduct)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper1 = ProductCustomizationWrapper.builder()
                .id(6L)
                .type(ProductCustomizationWrapperType.SINGLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization1 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper1)
                .id(6L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper2 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.MULTIPLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization2 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper2)
                .id(7L)
                .build();
        final ProductCustomizationWrapper shipmentProductCustomizationCustomizationWrapper3 = ProductCustomizationWrapper.builder()
                .id(7L)
                .type(ProductCustomizationWrapperType.MULTIPLE)
                .build();
        final ProductCustomization shipmentProductCustomizationCustomization3 = ProductCustomization.builder()
                .wrapper(shipmentProductCustomizationCustomizationWrapper3)
                .id(8L)
                .build();
        final ShipmentProductCustomization shipmentProductCustomization1 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization1)
                .price(BigDecimal.valueOf(5)).build();
        final ShipmentProductCustomization shipmentProductCustomization2 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization2)
                .price(BigDecimal.valueOf(6)).build();
        final ShipmentProductCustomization shipmentProductCustomization3 = ShipmentProductCustomization.builder()
                .productCustomization(shipmentProductCustomizationCustomization3)
                .price(BigDecimal.valueOf(7)).build();
        final ShipmentProduct shipmentProduct = ShipmentProduct.builder()
                .routeProduct(routeProduct)
                .customizations(Set.of(shipmentProductCustomization1, shipmentProductCustomization2, shipmentProductCustomization3))
                .quantity(BigDecimal.valueOf(55.22))
                .product(Product.builder().unitFraction(BigDecimal.valueOf(27)).build())
                .build();

        final Shipment shipment = Shipment.builder()
                .drop(drop)
                .products(Set.of(shipmentProduct))
                .build();

        doNothing().when(dropValidationService).validateDropForShipment(drop);
        //when
        final Throwable throwable = catchThrowable(() -> shipmentValidationService.validateShipment(shipment));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
        assertThat(((RestIllegalRequestValueException) throwable).getCode()).isEqualTo(RestExceptionStatusCode.INVALID_SHIPMENT_PRODUCT_QUANTITY_NOT_DIVIDABLE_BY_UNIT_FRACTION.ordinal());
    }
}