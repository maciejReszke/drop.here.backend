package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropRepository extends JpaRepository<Drop, Long> {

    @Query("select d from Drop d " +
            "join fetch d.spot where " +
            "d.route = :route")
    List<Drop> findByRouteWithSpot(Route route);
}
