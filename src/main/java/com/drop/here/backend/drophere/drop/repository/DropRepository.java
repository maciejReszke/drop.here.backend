package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.drop.dto.DropCustomerShortResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.spot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DropRepository extends JpaRepository<Drop, Long> {

    @Query("select d from Drop d " +
            "join fetch d.spot where " +
            "d.route = :route")
    List<Drop> findByRouteWithSpot(Route route);

    List<Drop> findBySpotAndStartTimeAfterAndStartTimeBefore(Spot spot, LocalDateTime from, LocalDateTime to);
}
