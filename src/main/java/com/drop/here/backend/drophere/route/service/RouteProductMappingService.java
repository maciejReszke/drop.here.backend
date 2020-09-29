package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.service.ProductSearchingService;
import com.drop.here.backend.drophere.route.dto.RouteProductResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteProductMappingService {
    private final ProductSearchingService productSearchingService;
    private final RouteProductRepository routeProductRepository;

    public List<RouteProductResponse> toProductResponses(Drop drop) {
        final List<RouteProduct> routeProducts = routeProductRepository.findByRouteDropContains(drop);
        return toResponses(routeProducts);
    }

    public List<RouteProductResponse> toProductResponses(Route route) {
        final List<RouteProduct> routeProducts = routeProductRepository.findByRoute(route);
        return toResponses(routeProducts);
    }

    private List<RouteProductResponse> toResponses(List<RouteProduct> routeProducts) {
        final List<Long> productsIds = routeProducts.stream()
                .map(RouteProduct::getProduct)
                .map(Product::getId)
                .collect(Collectors.toList());

        final List<ProductResponse> products = productSearchingService.findProducts(productsIds);

        return routeProducts.stream()
                .sorted(Comparator.comparing(RouteProduct::getOrderNum, Integer::compareTo))
                .map(routeProduct -> toRouteProductResponse(routeProduct, findProductForRouteProduct(products, routeProduct)))
                .collect(Collectors.toList());
    }

    private ProductResponse findProductForRouteProduct(List<ProductResponse> products, RouteProduct routeProduct) {
        return products.stream()
                .filter(productResponse -> productResponse.getId().equals(routeProduct.getProduct().getId()))
                .findFirst()
                .orElseThrow();
    }

    private RouteProductResponse toRouteProductResponse(RouteProduct routeProduct, ProductResponse productResponse) {
        return RouteProductResponse.builder()
                .amount(routeProduct.getAmount())
                .id(routeProduct.getId())
                .limitedAmount(routeProduct.isLimitedAmount())
                .price(routeProduct.getPrice())
                .productResponse(productResponse)
                .build();
    }
}
