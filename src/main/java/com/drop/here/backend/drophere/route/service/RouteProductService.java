package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.dto.RouteProductAmountChange;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class RouteProductService {
    private final RouteProductRepository routeProductRepository;
    private final RouteRepository routeRepository;

    public List<RouteProduct> findProducts(Drop drop, Set<Long> routeProductsIds) {
        return routeProductRepository.findJoinProductByRouteDropContainsAndRouteProductIds(drop, routeProductsIds);
    }

    // TODO: 18/10/2020 (rzucac wyjatek gdy sie nie uda)
    public void changeAmount(List<RouteProductAmountChange> routeProductChanges) {


    }
}
