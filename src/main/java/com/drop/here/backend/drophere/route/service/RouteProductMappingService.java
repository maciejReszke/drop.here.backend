package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.service.ProductSearchingService;
import com.drop.here.backend.drophere.route.dto.RouteProductRouteResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteProductMappingService {
    private final ProductSearchingService productSearchingService;
    private final RouteProductRepository routeProductRepository;

    public List<RouteProductRouteResponse> toProductResponses(Drop drop) {
        final List<RouteProduct> routeProducts = routeProductRepository.findByRouteDropContains(drop);
        return toResponses(routeProducts);
    }

    public List<RouteProductRouteResponse> toProductResponses(Route route) {
        final List<RouteProduct> routeProducts = routeProductRepository.findByRoute(route);
        return toResponses(routeProducts);
    }

    private List<RouteProductRouteResponse> toResponses(List<RouteProduct> routeProducts) {
        final List<Long> routeProductsIds = routeProducts.stream()
                .map(RouteProduct::getProduct)
                .map(Product::getId)
                .collect(Collectors.toList());

        final List<Long> originalProductsIds = routeProducts.stream()
                .map(RouteProduct::getOriginalProduct)
                .filter(Objects::nonNull)
                .map(Product::getId)
                .collect(Collectors.toList());

        final List<ProductResponse> forRouteProducts = productSearchingService.findProducts(routeProductsIds);
        final List<ProductResponse> originalProducts = productSearchingService.findProducts(originalProductsIds);

        return routeProducts.stream()
                .sorted(Comparator.comparing(RouteProduct::getOrderNum, Integer::compareTo))
                .map(routeProduct -> toRouteProductResponse(routeProduct,
                        findProductForRouteProduct(forRouteProducts, productResponse -> productResponse.getId().equals(routeProduct.getProduct().getId())),
                        findProductForRouteProduct(originalProducts, productResponse -> routeProduct.getOriginalProduct() != null && productResponse.getId().equals(routeProduct.getOriginalProduct().getId()))))
                .collect(Collectors.toList());
    }

    private ProductResponse findProductForRouteProduct(List<ProductResponse> products, Predicate<ProductResponse> productMatcher) {
        return products.stream()
                .filter(productMatcher)
                .findFirst()
                .orElse(null);
    }

    private RouteProductRouteResponse toRouteProductResponse(RouteProduct routeProduct, ProductResponse routeProductResponse, ProductResponse originalProductResponse) {
        return RouteProductRouteResponse.builder()
                .amount(routeProduct.getAmount())
                .id(routeProduct.getId())
                .limitedAmount(routeProduct.isLimitedAmount())
                .price(routeProduct.getPrice())
                .routeProductResponse(routeProductResponse)
                .originalProductResponse(originalProductResponse)
                .build();
    }
}
