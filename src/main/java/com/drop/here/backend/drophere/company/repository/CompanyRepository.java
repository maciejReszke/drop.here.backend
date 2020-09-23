package com.drop.here.backend.drophere.company.repository;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// TODO MONO:
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUid(String companyUid);

    Optional<Company> findByAccount(Account principal);

    @Query("select c from Company c " +
            "join fetch c.image i where " +
            "c.uid =:companyUid")
    Optional<Company> findByUidWithImage(String companyUid);
}
