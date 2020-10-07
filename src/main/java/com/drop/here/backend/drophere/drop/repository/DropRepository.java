package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.spot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DropRepository extends JpaRepository<Drop, Long> {

    @Query("select d from Drop d " +
            "join fetch d.spot where " +
            "d.route = :route")
    List<Drop> findByRouteWithSpot(Route route);

    List<Drop> findBySpotAndStartTimeAfterAndStartTimeBefore(Spot spot, LocalDateTime from, LocalDateTime to);

    @Query("select d from Drop d " +
            "join d.spot s " +
            "join d.spot.company c where " +
            "d.uid = :dropUid and " +
            "(" +
            "   c.visibilityStatus = 'VISIBLE'" +
            ") and " +
            "(" +
            "   s.hidden = false or s in (select sm.spot from SpotMembership sm " +
            "                                   where sm.spot = s and sm.customer = :customer " +
            "                                   and sm.membershipStatus = 'ACTIVE')" +
            ") and " +
            "(" +
            "   :customer not in (select ccr.customer from CompanyCustomerRelationship ccr" +
            "                       where ccr.customer = :customer and " +
            "                             ccr.company = c and " +
            "                             ccr.relationshipStatus = 'BLOCKED')" +
            ") and " +
            "(" +
            "   :customer not in (select dm.customer from SpotMembership dm " +
            "                      where dm.spot = s and dm.customer =:customer and " +
            "                       dm.membershipStatus = 'BLOCKED')" +
            ")")
    Optional<Drop> findPrivilegedDrop(String dropUid, Customer customer);

    @Query("select case when (count(d) > 0) then true else false end from Drop d " +
            "join d.spot s " +
            "join s.company c where " +
            "d.route.profile.profileUid = :profileUid and " +
            "d.route.status = 'LIVE' and " +
            "(" +
            "   c.visibilityStatus = 'VISIBLE'" +
            ") and " +
            "(" +
            "   s in (select sm.spot from SpotMembership sm " +
            "         where sm.spot = s and sm.customer = :customer " +
            "         and sm.membershipStatus = 'ACTIVE')" +
            ") and " +
            "(" +
            "   :customer not in (select ccr.customer from CompanyCustomerRelationship ccr" +
            "                       where ccr.customer = :customer and " +
            "                             ccr.company = c and " +
            "                             ccr.relationshipStatus = 'BLOCKED')" +
            ") and " +
            "(" +
            "   :customer not in (select dm.customer from SpotMembership dm " +
            "                      where dm.spot = s and dm.customer =:customer and " +
            "                       dm.membershipStatus = 'BLOCKED')" +
            ")")
    boolean isSellerLocationAvailableForCustomer(String profileUid, Customer customer);

    @Query("select d from Drop d " +
            "join fetch d.spot " +
            "where d.uid = :dropUid and " +
            "d.route.company = :company")
    Optional<Drop> findByUidAndRouteCompanyWithSpot(String dropUid, Company company);
}
