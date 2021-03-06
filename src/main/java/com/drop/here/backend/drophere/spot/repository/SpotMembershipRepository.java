package com.drop.here.backend.drophere.spot.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotMembershipRepository extends JpaRepository<SpotMembership, Long> {
    Optional<SpotMembership> findBySpotAndCustomer(Spot spot, Customer customer);

    @Modifying
    void deleteBySpot(Spot spot);

    Optional<SpotMembership> findByIdAndSpot(Long id, Spot spot);

    @Query(value = "select sm from SpotMembership sm " +
            "join fetch sm.customer c where " +
            "sm.spot = :spot and " +
            "sm.membershipStatus in (:membershipStatuses) and " +
            "(:desiredCustomerSubstring is null or " +
            "   lower(c.firstName) like :desiredCustomerSubstring or " +
            "   lower(c.lastName) like :desiredCustomerSubstring)",
            countQuery = "select count(sm) from SpotMembership sm where " +
                    "sm.spot = :spot and " +
                    "sm.membershipStatus in (:membershipStatuses) and " +
                    "(:desiredCustomerSubstring is null or " +
                    "   lower(sm.customer.firstName) like :desiredCustomerSubstring or " +
                    "   lower(sm.customer.lastName) like :desiredCustomerSubstring)")
    Page<SpotMembership> findMembershipsWithCustomers(Spot spot, String desiredCustomerSubstring, SpotMembershipStatus[] membershipStatuses, Pageable pageable);

    boolean existsBySpotCompanyAndCustomerId(Company company, Long customerId);

    @Query(value = "select sm from SpotMembership sm " +
            "join fetch sm.spot where " +
            "sm.spot.company =:company and " +
            "sm.customer.id in (:customersIds)",
            countQuery = "select count(sm) from SpotMembership sm " +
                    "join sm.spot where " +
                    "sm.spot.company =:company and " +
                    "sm.customer.id in (:customersIds)")
    List<SpotMembership> findBySpotCompanyAndCustomerIdInJoinFetchSpots(Company company, List<Long> customersIds);

    List<SpotMembership> findByCustomerAndSpotIn(Customer customer, List<Spot> spots);

    Optional<SpotMembership> findByCustomerAndSpot(Customer customer, Spot spot);

    @Query("select sm from SpotMembership sm where " +
            "sm.spot = :spot and " +
            "sm.customer not in (select dm.customer from SpotMembership dm " +
            "                      where dm.spot = :spot and dm.customer = sm.customer and " +
            "                       dm.membershipStatus = 'BLOCKED') and " +
            "(:prepared = false or sm.receivePreparedNotifications = true) and" +
            "(:live = false or sm.receiveLiveNotifications = true) and" +
            "(:finished = false or sm.receiveFinishedNotifications = true) and" +
            "(:delayed = false or sm.receiveDelayedNotifications = true) and" +
            "(:cancelled = false or sm.receiveCancelledNotifications = true)")
    List<SpotMembership> findToBeNotified(Spot spot, boolean prepared, boolean live, boolean finished, boolean delayed, boolean cancelled);
}
