package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.route.dto.RouteDropRequest;
import com.drop.here.backend.drophere.route.dto.RouteProductRequest;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class RouteDataGenerator {

    public RouteRequest request(int i) {
        return RouteRequest.builder()
                .date("2020-04-04")
                .description("description" + i)
                .drops(List.of(routeDropRequest(i)))
                .name("routeName" + i)
                .products(List.of(productRequest(2 * i), productRequest(2 * i + 1)))
                .profileUid("profileUid" + i)
                .build();
    }

    private RouteProductRequest productRequest(int i) {
        return RouteProductRequest
                .builder()
                .amount(BigDecimal.valueOf(55.4))
                .limitedAmount(true)
                .productId(5L + i)
                .price(BigDecimal.valueOf(55L))
                .build();
    }

    private RouteDropRequest routeDropRequest(int i) {
        return RouteDropRequest.builder()
                .description("dropDescription" + i)
                .endTime("17:35")
                .name("dropName" + i)
                .spotId(5L + i)
                .startTime("17:15")
                .build();
    }

    public Route route(int i, Company company) {
        return Route.builder()
                .status(RouteStatus.UNPREPARED)
                .company(company)
                .createdAt(LocalDateTime.now())
                .description("routeDescription" + i)
                .drops(List.of())
                .lastUpdatedAt(LocalDateTime.now())
                .products(List.of())
                .profile(null)
                .routeDate(LocalDate.now())
                .name("routeName" + i)
                .withSeller(false)
                .build();
    }

    public RouteProduct product(int i, Route preSavedRoute, Product product) {
        return RouteProduct.builder()
                .route(preSavedRoute)
                .product(product)
                .price(BigDecimal.valueOf(123.3d))
                .limitedAmount(true)
                .amount(BigDecimal.valueOf(51))
                .orderNum(i)
                .build();
    }
}
