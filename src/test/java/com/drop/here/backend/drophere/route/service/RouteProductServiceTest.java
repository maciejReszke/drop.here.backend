package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.route.dto.RouteProductAmountChange;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.exception.NotEnoughProductsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class RouteProductServiceTest {
    @InjectMocks
    private RouteProductService routeProductService;

    @Test
    void givenNotLimitedAmountProductWhenChangeAmountThenDoNothing() {
        //given
        final RouteProduct routeProduct = RouteProduct.builder().limitedAmount(false).amount(BigDecimal.valueOf(33.2d)).build();
        final List<RouteProductAmountChange> routeProductAmountChanges = List.of(new RouteProductAmountChange(
                routeProduct,
                BigDecimal.valueOf(24.23)));

        //when
        routeProductService.changeAmount(routeProductAmountChanges);

        //then
        assertThat(routeProduct.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(33.2d));
    }

    @Test
    void givenLimitedAmountProductAddLessThanWasWhenChangeAmountThenAdd() {
        //given
        final RouteProduct routeProduct = RouteProduct.builder().limitedAmount(true).amount(BigDecimal.valueOf(33.2d)).build();
        final List<RouteProductAmountChange> routeProductAmountChanges = List.of(new RouteProductAmountChange(
                routeProduct,
                BigDecimal.valueOf(24.23)));

        //when
        routeProductService.changeAmount(routeProductAmountChanges);

        //then
        assertThat(routeProduct.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(57.43));
    }

    @Test
    void givenLimitedAmountProductAddMoreThanWasWhenChangeAmountThenAdd() {
        //given
        final RouteProduct routeProduct = RouteProduct.builder().limitedAmount(true).amount(BigDecimal.valueOf(33.2d)).build();
        final List<RouteProductAmountChange> routeProductAmountChanges = List.of(new RouteProductAmountChange(
                routeProduct,
                BigDecimal.valueOf(44.23)));

        //when
        routeProductService.changeAmount(routeProductAmountChanges);

        //then
        assertThat(routeProduct.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(77.43));
    }


    @Test
    void givenLimitedAmountProductSubtractEnoughWhenChangeAmountThenSubtract() {
        //given
        final RouteProduct routeProduct = RouteProduct.builder().limitedAmount(true).amount(BigDecimal.valueOf(33.2d)).build();
        final List<RouteProductAmountChange> routeProductAmountChanges = List.of(new RouteProductAmountChange(
                routeProduct,
                BigDecimal.valueOf(-24.23)));

        //when
        routeProductService.changeAmount(routeProductAmountChanges);

        //then
        assertThat(routeProduct.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(8.97));
    }

    @Test
    void givenLimitedAmountProductSubtractNotEnoughWhenChangeAmountThenThrowException() {
        //given
        final RouteProduct routeProduct = RouteProduct.builder().limitedAmount(true).amount(BigDecimal.valueOf(33.2d)).build();
        final List<RouteProductAmountChange> routeProductAmountChanges = List.of(new RouteProductAmountChange(
                routeProduct,
                BigDecimal.valueOf(-44.23)));

        //when
        final Throwable throwable = catchThrowable(() -> routeProductService.changeAmount(routeProductAmountChanges));

        //then
        assertThat(throwable).isInstanceOf(NotEnoughProductsException.class);
    }
}