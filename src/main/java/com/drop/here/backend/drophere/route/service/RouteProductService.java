package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouteProductService {
    private final RouteProductRepository routeProductRepository;

    public List<RouteProduct> findProducts(Drop drop, Set<Long> routeProductsIds) {
        return routeProductRepository.findJoinProductByRouteDropContainsAndRouteProductIds(drop, routeProductsIds);
    }
}
