package com.drop.here.backend.drophere.customer.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

// TODO MONO:
@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    @Query("select distinct c from Customer c " +
            "left join CompanyCustomerRelationship ccr on (ccr.customer =c and ccr.company =:company) where " +
            "(ccr is not null or c in (select dm.customer from SpotMembership dm " +
            "                                             join dm.spot d where " +
            "                                             d.company = :company)) and " +
            "(" +
            "   :blocked is null or (:blocked = true and ccr.relationshipStatus = 'BLOCKED') or (:blocked = false and (ccr is null or ccr.relationshipStatus = 'ACTIVE'))" +
            ") and " +
            "(" +
            "   lower(c.firstName) like concat(lower(:desiredCustomerStartingSubstring), '%') or " +
            "   lower(c.lastName) like concat(lower(:desiredCustomerStartingSubstring), '%')" +
            ")")
    /*@Query("{$and:["
            + "?#{ [1] == null ? {$exists: true}} : "
            +""
            + "]}")*/
    Page<Customer> findCustomers(String desiredCustomerStartingSubstring, Boolean blocked, Company company, Pageable pageable);
}
