package com.drop.here.backend.drophere.authentication.account.repository;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByMail(String mail);

    @Query("select a from Account a join fetch a.privileges")
    Optional<Account> findByMailWithRoles(String mail);
}
