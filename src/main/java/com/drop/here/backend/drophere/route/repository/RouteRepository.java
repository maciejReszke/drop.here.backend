package com.drop.here.backend.drophere.route.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.route.dto.RouteShortResponse;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.route.enums.RouteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByIdAndCompany(Long routeId, Company company);

    @Query("select new com.drop.here.backend.drophere.route.dto.RouteShortResponse(" +
            "r.id, r.name, size(r.products), size(r.drops), r.profile.profileUid, r.profile.firstName, r.profile.lastName) " +
            "from Route r where " +
            "r.company =:company and " +
            ":routeStatus is null or r.status = :routeStatus")
    Page<RouteShortResponse> findByCompany(Company company, RouteStatus routeStatus, Pageable pageable);
}
