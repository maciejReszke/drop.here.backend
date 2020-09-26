package com.drop.here.backend.drophere.route.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfilePersistenceService;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.service.UidGeneratorService;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.enums.DropStatus;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.service.ProductSearchingService;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.route.dto.RouteDropRequest;
import com.drop.here.backend.drophere.route.dto.RouteProductRequest;
import com.drop.here.backend.drophere.route.dto.RouteProductResponse;
import com.drop.here.backend.drophere.route.dto.RouteRequest;
import com.drop.here.backend.drophere.route.dto.RouteResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
import com.drop.here.backend.drophere.spot.service.SpotPersistenceService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteMappingService {
    private final AccountProfilePersistenceService accountProfilePersistenceService;
    private final SpotPersistenceService spotPersistenceService;
    private final ProductService productService;
    private final UidGeneratorService uidGeneratorService;
    private final ProductSearchingService productSearchingService;
    private final DropService dropService;
    private final RouteProductRepository routeProductRepository;

    private static final String TIME_PATTERN = "HH:mm";

    @Value("${drops.uid_generator.name_part_length}")
    private int namePartLength;

    @Value("${drops.uid_generator.random_part_length}")
    private int randomPartLength;

    public Route toRoute(RouteRequest routeRequest, Company company) {
        final Route route = Route.builder()
                .createdAt(LocalDateTime.now())
                .status(RouteStatus.PREPARED)
                .company(company)
                .drops(new ArrayList<>())
                .products(new ArrayList<>())
                .build();
        updateRoute(route, routeRequest, company);
        return route;
    }

    public void updateRoute(Route route, RouteRequest routeRequest, Company company) {
        route.getProducts().forEach(product -> product.setRoute(null));
        route.getProducts().clear();
        route.getDrops().forEach(drop -> drop.setRoute(null));
        route.getDrops().clear();
        route.setLastUpdatedAt(LocalDateTime.now());
        route.setName(routeRequest.getName().strip());
        route.setDescription(routeRequest.getDescription());
        route.setRouteDate(LocalDate.parse(routeRequest.getDate().strip(), DateTimeFormatter.ISO_LOCAL_DATE));
        buildProducts(routeRequest, route, company).forEach(product -> route.getProducts().add(product));
        buildDrops(routeRequest, route, company).forEach(drop -> route.getDrops().add(drop));
        route.setProfile(getProfile(company, routeRequest));
        route.setWithSeller(route.getProfile() != null);
    }

    private AccountProfile getProfile(Company company, RouteRequest routeRequest) {
        return StringUtils.isBlank(routeRequest.getProfileUid())
                ? null
                : accountProfilePersistenceService.findActiveByCompanyAndProfileUid(company, routeRequest.getProfileUid())
                .orElseThrow(() -> new RestEntityNotFoundException(String.format("Profile with uid %s was not found",
                        routeRequest.getProfileUid()),
                        RestExceptionStatusCode.ACCOUNT_PROFILE_NOT_FOUND_DURING_CREATING_ROUTE));
    }

    private List<Drop> buildDrops(RouteRequest routeRequest, Route route, Company company) {
        return routeRequest.getDrops().stream()
                .map(drop -> buildDrop(drop, route, company))
                .collect(Collectors.toList());
    }


    private Drop buildDrop(RouteDropRequest dropRequest, Route route, Company company) {
        return Drop.builder()
                .uid(generateUid(dropRequest.getName().strip()))
                .name(dropRequest.getName().strip())
                .description(dropRequest.getDescription())
                .spot(spotPersistenceService.findSpot(dropRequest.getSpotId(), company))
                .startTime(LocalTime.parse(dropRequest.getStartTime(), DateTimeFormatter.ofPattern(TIME_PATTERN)).atDate(route.getRouteDate()))
                .endTime(LocalTime.parse(dropRequest.getEndTime(), DateTimeFormatter.ofPattern(TIME_PATTERN)).atDate(route.getRouteDate()))
                .createdAt(LocalDateTime.now())
                .route(route)
                .status(DropStatus.PREPARED)
                .build();
    }

    private List<RouteProduct> buildProducts(RouteRequest routeRequest, Route route, Company company) {
        final AtomicInteger counter = new AtomicInteger(0);
        return routeRequest.getProducts()
                .stream()
                .map(routeProductRequest -> buildProduct(routeProductRequest, route, company, counter.incrementAndGet()))
                .collect(Collectors.toList());
    }

    private RouteProduct buildProduct(RouteProductRequest routeProductRequest, Route route, Company company, int orderNum) {
        final Product product = productService.getProduct(routeProductRequest.getProductId(), company.getUid());
        return RouteProduct.builder()
                .orderNum(orderNum)
                .limitedAmount(routeProductRequest.isLimitedAmount())
                .amount(getAmount(routeProductRequest))
                .price(routeProductRequest.getPrice().setScale(2, RoundingMode.DOWN))
                .product(product)
                .route(route)
                .build();
    }

    private BigDecimal getAmount(RouteProductRequest routeProductRequest) {
        return routeProductRequest.isLimitedAmount()
                ? routeProductRequest.getAmount()
                : BigDecimal.ZERO;
    }

    public RouteResponse toRouteResponse(Route route) {
        final List<DropRouteResponse> drops = dropService.toDropResponses(route);
        final List<RouteProductResponse> products = toProductResponses(route);
        final Optional<AccountProfile> profile = getProfile(route);
        return RouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .description(route.getDescription())
                .status(route.getStatus())
                .productsAmount(products.size())
                .dropsAmount(drops.size())
                .profileUid(profile.map(AccountProfile::getProfileUid).orElse(null))
                .profileFirstName(profile.map(AccountProfile::getFirstName).orElse(null))
                .profileLastName(profile.map(AccountProfile::getLastName).orElse(null))
                .products(products)
                .drops(drops)
                .routeDate(route.getRouteDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();
    }


    private Optional<AccountProfile> getProfile(Route route) {
        return route.isWithSeller()
                ? Optional.of(accountProfilePersistenceService.findById(route.getProfile().getId()))
                : Optional.empty();
    }

    private List<RouteProductResponse> toProductResponses(Route route) {
        final List<RouteProduct> routeProducts = routeProductRepository.findByRoute(route);

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

    private String generateUid(String name) {
        return uidGeneratorService.generateUid(name, namePartLength, randomPartLength);
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
                .limitedAmount(routeProduct.isLimitedAmount())
                .price(routeProduct.getPrice())
                .productResponse(productResponse)
                .build();
    }
}
