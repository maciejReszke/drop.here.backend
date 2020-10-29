package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.dto.RouteProductAmountChange;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.exception.NotEnoughProductsException;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class RouteProductService {
    private final RouteProductRepository routeProductRepository;
    private final RoutePersistenceService routePersistenceService;

    public List<RouteProduct> findProductsLocked(Drop drop, Set<Long> routeProductsIds) {
        routePersistenceService.findLocked(drop.getRoute().getId());
        return routeProductRepository.findJoinProductByRouteDropContainsAndRouteProductIds(drop, routeProductsIds);
    }

    public void changeAmount(List<RouteProductAmountChange> routeProductChanges) {
        routeProductChanges
                .stream()
                .filter(routeProduct -> routeProduct.getRouteProduct().isLimitedAmount())
                .forEach(this::changeProductAmount);
    }

    private void changeProductAmount(RouteProductAmountChange routeProductChange) {
        final RouteProduct product = routeProductChange.getRouteProduct();
        final BigDecimal currentAmount = product.getAmount();
        final BigDecimal changeAmount = routeProductChange.getAmountChange();
        if (changeAmount.signum() < 0 && currentAmount.compareTo(changeAmount.abs()) < 0) {
            throw new NotEnoughProductsException(String.format(
                    "Cannot order route product %s because amount is %s and order wants %s",
                    product.getId(),
                    currentAmount,
                    changeAmount));
        }
        product.setAmount(currentAmount.add(changeAmount));
    }
}
