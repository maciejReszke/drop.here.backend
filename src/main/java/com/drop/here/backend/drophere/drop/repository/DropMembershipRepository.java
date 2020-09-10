package com.drop.here.backend.drophere.drop.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DropMembershipRepository extends JpaRepository<DropMembership, Long> {
    Optional<DropMembership> findByDropAndCustomer(Drop drop, Customer customer);

    @Modifying
    void deleteByDrop(Drop drop);

    Optional<DropMembership> findByIdAndDrop(Long id, Drop drop);

    @Query(value = "select dm from DropMembership dm " +
            "join fetch dm.customer c where " +
            "dm.drop = :drop and " +
            "dm.membershipStatus in (:membershipStatuses) and " +
            "(:desiredCustomerSubstring is null or " +
            "   lower(c.firstName) like :desiredCustomerSubstring or " +
            "   lower(c.lastName) like :desiredCustomerSubstring)",
            countQuery = "select count(dm) from DropMembership dm where " +
                    "dm.drop = :drop and " +
                    "dm.membershipStatus in (:membershipStatuses) and " +
                    "(:desiredCustomerSubstring is null or " +
                    "   lower(dm.customer.firstName) like :desiredCustomerSubstring or " +
                    "   lower(dm.customer.lastName) like :desiredCustomerSubstring)")
    Page<DropMembership> findMembershipsWithCustomers(Drop drop, String desiredCustomerSubstring, DropMembershipStatus[] membershipStatuses, Pageable pageable);

    boolean existsByDropCompanyAndCustomerId(Company company, Long customerId);

    @Query(value = "select dm from DropMembership dm " +
            "join fetch dm.drop where " +
            "dm.drop.company =:company and " +
            "dm.customer.id in (:customersIds)",
            countQuery = "select count(dm) from DropMembership dm " +
                    "join dm.drop where " +
                    "dm.drop.company =:company and " +
                    "dm.customer.id in (:customersIds)")
    List<DropMembership> findByDropCompanyAndCustomerIdInJoinFetchDrops(Company company, List<Long> customersIds);

    List<DropMembership> findByCustomerAndDropIn(Customer customer, List<Drop> drops);
}
