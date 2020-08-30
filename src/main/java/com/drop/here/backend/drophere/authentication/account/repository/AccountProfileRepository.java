package com.drop.here.backend.drophere.authentication.account.repository;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountProfileRepository extends JpaRepository<AccountProfile, Long> {

    @Query("select ap from AccountProfile ap " +
            "join fetch ap.privileges where " +
            "ap.account = :account and " +
            "ap.profileUid = :profileUid")
    Optional<AccountProfile> findByAccountAndProfileUidWithRoles(Account account, String profileUid);

    List<AccountProfile> findByAccount(Account account);
}
