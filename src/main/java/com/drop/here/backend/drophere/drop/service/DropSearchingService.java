package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropCustomerSpotResponse;
import com.drop.here.backend.drophere.drop.dto.DropProductResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.dto.RouteProductProductResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.entity.Spot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropSearchingService {
    private final DropRepository dropRepository;
    private final RouteProductRepository routeProductRepository;

    public List<DropCustomerSpotResponse> findDrops(Spot spot, LocalDateTime from, LocalDateTime to) {
        return dropRepository.findJoinedRouteBySpotAndStartTimeAfterAndEndTimeBefore(spot, from, to)
                .stream()
                .map(this::toDropCustomerSpotResponse)
                .collect(Collectors.toList());
    }

    private DropCustomerSpotResponse toDropCustomerSpotResponse(Drop drop) {
        return DropCustomerSpotResponse
                .builder()
                .description(drop.getDescription())
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .name(drop.getName())
                .status(drop.getStatus())
                .uid(drop.getUid())
                .acceptShipmentsAutomatically(drop.getRoute().isAcceptShipmentsAutomatically())
                .build();
    }

    public List<DropProductResponse> findProductDrops(List<Long> productsIds, AccountAuthentication authentication) {
        final List<RouteProduct> products = routeProductRepository.findByOriginalProductIdIn(productsIds);

        final Set<Long> routesIds = products.stream()
                .map(RouteProduct::getRoute)
                .map(Route::getId)
                .collect(Collectors.toSet());

        final List<Drop> drops = authentication.getPrincipal().getAccountType() == AccountType.COMPANY
                ? findCompanyProductDrops(routesIds)
                : findCustomerProductDrops(routesIds, authentication.getCustomer());

        return drops.stream()
                .sorted(Comparator.comparing(Drop::getStartTime))
                .map(drop -> toDropProductResponse(drop, findDropProduct(drop, products)))
                .collect(Collectors.toList());
    }


    private RouteProduct findDropProduct(Drop drop, List<RouteProduct> products) {
        return products.stream()
                .filter(product -> product.getRoute().getId().equals(drop.getRoute().getId()))
                .findFirst()
                .orElseThrow();
    }

    private DropProductResponse toDropProductResponse(Drop drop, RouteProduct routeProduct) {
        return DropProductResponse.builder()
                .uid(drop.getUid())
                .name(drop.getName())
                .description(drop.getDescription())
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(drop.getStatus())
                .spotXCoordinate(drop.getSpot().getXCoordinate())
                .spotYCoordinate(drop.getSpot().getYCoordinate())
                .spotEstimatedRadiusMeters(drop.getSpot().getEstimatedRadiusMeters())
                .spotName(drop.getSpot().getName())
                .spotDescription(drop.getSpot().getDescription())
                .spotUid(drop.getSpot().getUid())
                .routeProduct(toRouteProductResponse(routeProduct))
                .build();
    }

    private RouteProductProductResponse toRouteProductResponse(RouteProduct routeProduct) {
        return RouteProductProductResponse.builder()
                .amount(routeProduct.getAmount())
                .id(routeProduct.getId())
                .originalProductId(routeProduct.getOriginalProduct().getId())
                .limitedAmount(routeProduct.isLimitedAmount())
                .price(routeProduct.getPrice())
                .build();
    }

    private List<Drop> findCustomerProductDrops(Set<Long> routesIds, Customer customer) {
        return dropRepository.findUpcomingByRouteIdInWithSpotForCustomer(routesIds, customer);
    }

    private List<Drop> findCompanyProductDrops(Set<Long> routesIds) {
        return dropRepository.findUpcomingByRouteIdInWithSpotForCompany(routesIds);
    }
}
