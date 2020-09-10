package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DropRepository extends JpaRepository<Drop, Long> {
    Optional<Drop> findByIdAndCompanyUid(Long dropId, String companyUid);

    Optional<Drop> findByUidAndCompanyUid(String dropUid, String companyUid);

    List<Drop> findAllByCompanyUidAndNameStartsWith(String companyUid, String name);

    // 0.000009009 = 1 meter to 1 deg ratio
    @Query("select d from Drop d " +
            "join fetch d.company c where " +
            "(" +
            "   c.visibilityStatus = 'VISIBLE'" +
            ") and " +
            "(" +
            "   sqrt((d.xCoordinate - :xCoordinate) * (d.xCoordinate - :xCoordinate) + (d.yCoordinate - :yCoordinate) * (d.yCoordinate - :yCoordinate)) < 0.000009009 * (d.estimatedRadiusMeters + :radius)" +
            ") and " +
            "(" +
            "   lower(d.name) like lower(concat(:namePrefix, '%')) or " +
            "   lower(d.company.name) like lower(concat(:namePrefix, '%'))" +
            ") and " +
            "(" +
            "   d.hidden = false or d in (select dm.drop from DropMembership dm " +
            "                                   where dm.drop = d and dm.customer = :customer)" +
            ") and " +
            "(" +
            "   :customer not in (select ccr.customer from CompanyCustomerRelationship ccr" +
            "                       where ccr.customer = :customer and " +
            "                             ccr.company = c and " +
            "                             ccr.relationshipStatus = 'BLOCKED')" +
            ") and " +
            "(" +
            "   :customer not in (select dm.customer from DropMembership dm " +
            "                      where dm.drop = d and dm.customer =:customer and " +
            "                       dm.membershipStatus = 'BLOCKED')" +
            ") and " +
            "(" +
            "   :member is null or " +
            "   (:member = true and d in (select dm.drop from DropMembership dm " +
            "                                  where dm.drop = d and dm.customer = :customer)) or " +
            "   (:member = false and d not in (select dm.drop from DropMembership dm " +
            "                                   where dm.drop = d and dm.customer = :customer))" +
            ")"
    )
    List<Drop> findDrops(Customer customer, Double xCoordinate, Double yCoordinate, Integer radius, Boolean member, String namePrefix, Sort sort);
}
