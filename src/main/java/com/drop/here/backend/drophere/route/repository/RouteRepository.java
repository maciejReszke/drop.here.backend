package com.drop.here.backend.drophere.route.repository;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.entity.Drop;
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
            "r.id, r.name, size(r.products), size(r.drops), p.profileUid, p.firstName, p.lastName) " +
            "from Route r " +
            "left join r.profile p where " +
            "r.company =:company and " +
            ":routeStatus is null or r.status = :routeStatus")
    Page<RouteShortResponse> findByCompany(Company company, RouteStatus routeStatus, Pageable pageable);

    @Query("select case when (count(r) > 0) then true else false end from Route r where " +
            "r.profile = :profile and " +
            ":drop in (select d from Drop d where d.route = r)")
    boolean existsByProfileAndContainsDrop(AccountProfile profile, Drop drop);

    boolean existsByStatusAndProfile(RouteStatus status, AccountProfile profile);
}
