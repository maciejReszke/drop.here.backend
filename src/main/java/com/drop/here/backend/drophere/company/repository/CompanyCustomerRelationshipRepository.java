package com.drop.here.backend.drophere.company.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.company.enums.CompanyCustomerRelationshipStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CompanyCustomerRelationshipRepository extends ReactiveMongoRepository<CompanyCustomerRelationship, String> {
    Mono<CompanyCustomerRelationship> findByCompanyAndCustomerAndRelationshipStatus(Company company, Customer customer, CompanyCustomerRelationshipStatus blocked);

    Mono<CompanyCustomerRelationship> findByCompanyAndCustomerId(Company company, String customerId);

    List<CompanyCustomerRelationship> findByCompanyAndCustomerIdIn(Company company, List<String> customersIds);
}
