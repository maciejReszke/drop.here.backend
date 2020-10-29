package com.drop.here.backend.drophere.route.repository;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RouteProductRepository extends JpaRepository<RouteProduct, Long> {

    List<RouteProduct> findByRoute(Route route);

    @Query("select rp from RouteProduct rp where " +
            "rp.route = (select d.route from Drop d where d =:drop)")
    List<RouteProduct> findByRouteDropContains(Drop drop);

    @Query("select rp from RouteProduct rp where " +
            "rp.id in (:routeProductsIds) and " +
            "rp.route = (select d.route from Drop d where d =:drop)")
    List<RouteProduct> findJoinProductByRouteDropContainsAndRouteProductIds(Drop drop, Set<Long> routeProductsIds);

    List<RouteProduct> findByOriginalProductIdIn(List<Long> productsIds);

    @Modifying
    @Query("update RouteProduct rp " +
            "set rp.originalProduct = null where " +
            "rp.originalProduct.id = :productId")
    void nullOriginalProductId(Long productId);
}
