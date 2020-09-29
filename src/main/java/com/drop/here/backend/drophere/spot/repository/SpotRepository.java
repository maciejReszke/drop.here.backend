package com.drop.here.backend.drophere.spot.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.spot.entity.Spot;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    Optional<Spot> findByIdAndCompanyUid(Long spotId, String companyUid);

    Optional<Spot> findByUidAndCompanyUid(String spotUid, String companyUid);

    List<Spot> findAllByCompanyUidAndNameStartsWith(String companyUid, String name);

    // 0.000009009 = 1 meter to 1 deg ratio
    @Query("select d from Spot d " +
            "join fetch d.company c where " +
            "(" +
            "   c.visibilityStatus = 'VISIBLE'" +
            ") and " +
            "(" +
            "   d.hidden = false or d in (select dm.spot from SpotMembership dm " +
            "                                   where dm.spot = d and dm.customer = :customer and " +
            "                                   dm.membershipStatus = 'ACTIVE')" +
            ") and " +
            "(" +
            "   :customer not in (select ccr.customer from CompanyCustomerRelationship ccr" +
            "                       where ccr.customer = :customer and " +
            "                             ccr.company = c and " +
            "                             ccr.relationshipStatus = 'BLOCKED')" +
            ") and " +
            "(" +
            "   :customer not in (select dm.customer from SpotMembership dm " +
            "                      where dm.spot = d and dm.customer =:customer and " +
            "                       dm.membershipStatus = 'BLOCKED')" +
            ") and " +
            "(" +
            "   :member is null or " +
            "   (:member = true and d in (select dm.spot from SpotMembership dm " +
            "                                  where dm.spot = d and dm.customer = :customer)) or " +
            "   (:member = false and d not in (select dm.spot from SpotMembership dm " +
            "                                   where dm.spot = d and dm.customer = :customer))" +
            ") and " +
            "(" +
            "   lower(d.name) like lower(concat(:namePrefix, '%')) or " +
            "   lower(d.company.name) like lower(concat(:namePrefix, '%'))" +
            ") and " +
            "(" +
            "   sqrt((d.xCoordinate - :xCoordinate) * (d.xCoordinate - :xCoordinate) + (d.yCoordinate - :yCoordinate) * (d.yCoordinate - :yCoordinate)) < 0.000009009 * (d.estimatedRadiusMeters + :radius)" +
            ")"
    )
    List<Spot> findSpots(Customer customer, Double xCoordinate, Double yCoordinate, Integer radius, Boolean member, String namePrefix, Sort sort);

    Optional<Spot> findByIdAndCompany(Long spotId, Company company);

    @Query("select d from Spot d " +
            "join fetch d.company c where " +
            "d.uid = :spotUid and " +
            "(" +
            "   c.visibilityStatus = 'VISIBLE'" +
            ") and " +
            "(" +
            "   d.hidden = false or d in (select dm.spot from SpotMembership dm " +
            "                                   where dm.spot = d and dm.customer = :customer " +
            "                                   and dm.membershipStatus = 'ACTIVE')" +
            ") and " +
            "(" +
            "   :customer not in (select ccr.customer from CompanyCustomerRelationship ccr" +
            "                       where ccr.customer = :customer and " +
            "                             ccr.company = c and " +
            "                             ccr.relationshipStatus = 'BLOCKED')" +
            ") and " +
            "(" +
            "   :customer not in (select dm.customer from SpotMembership dm " +
            "                      where dm.spot = d and dm.customer =:customer and " +
            "                       dm.membershipStatus = 'BLOCKED')" +
            ")")
    Optional<Spot> findPrivilegedSpot(String spotUid, Customer customer);

    @Query("select d from Spot d " +
            "join fetch d.company c where " +
            "(" +
            "   c.visibilityStatus = 'VISIBLE'" +
            ") and " +
            "(" +
            "  d in (select dm.spot from SpotMembership dm " +
            "                           where dm.spot = d " +
            "                           and dm.customer = :customer " +
            "                           and dm.membershipStatus != 'BLOCKED')" +
            ") and " +
            "(" +
            "   :customer not in (select ccr.customer from CompanyCustomerRelationship ccr" +
            "                       where ccr.customer = :customer and " +
            "                             ccr.company = c and " +
            "                             ccr.relationshipStatus = 'BLOCKED')" +
            ")")
    List<Spot> findSpots(Customer customer);

    @Query("select s from Spot s " +
            "join fetch s.company where " +
            "s.id = :id ")
    Optional<Spot> findByIdWithCompany(Long id);
}
