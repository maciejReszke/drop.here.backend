package com.drop.here.backend.drophere.route.repository;

import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.entity.RouteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteProductRepository extends JpaRepository<RouteProduct, Long> {

    List<RouteProduct> findByRoute(Route route);
}
