package com.drop.here.backend.drophere.company.repository;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.entity.Company;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CompanyRepository extends ReactiveMongoRepository<Company, Long> {
    Mono<Company> findByUid(String companyUid);

    Mono<Company> findByAccount(Account principal);
}
