package com.drop.here.backend.drophere.spot.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO MONO:
@Repository
public interface SpotMembershipRepository extends ReactiveMongoRepository<SpotMembership, Long> {
    Mono<SpotMembership> findBySpotAndCustomer(Spot spot, Customer customer);

    @Modifying
    Mono<Void> deleteBySpot(Spot spot);

    Mono<SpotMembership> findByIdAndSpot(Long id, Spot spot);

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
    Flux<SpotMembership> findMembershipsWithCustomers(Spot spot, String desiredCustomerSubstring, SpotMembershipStatus[] membershipStatuses, Pageable pageable);

    Mono<SpotMembership> existsBySpotCompanyAndCustomerId(Company company, String customerId);

    @Query(value = "select sm from SpotMembership sm " +
            "join fetch sm.spot where " +
            "sm.spot.company =:company and " +
            "sm.customer.id in (:customersIds)",
            countQuery = "select count(sm) from SpotMembership sm " +
                    "join sm.spot where " +
                    "sm.spot.company =:company and " +
                    "sm.customer.id in (:customersIds)")
    Flux<SpotMembership> findBySpotCompanyAndCustomerIdInJoinFetchSpots(Company company, List<Long> customersIds);

    Flux<SpotMembership> findByCustomerAndSpotIn(Customer customer, List<Spot> spots);
}
