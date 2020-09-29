package com.drop.here.backend.drophere.authentication.account.repository;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.entity.Drop;
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

    @Query("select ap from AccountProfile ap " +
            "join fetch ap.image where " +
            "ap.profileUid = :profileUid")
    Optional<AccountProfile> findByProfileUidWithImage(String profileUid);

    @Query("select ap from AccountProfile ap " +
            "join ap.account a where " +
            "ap.profileUid =:profileUid and " +
            "ap.status =:status and " +
            ":company = (select c from Company c where c.account = a)")
    Optional<AccountProfile> findByAccountCompanyAndProfileUidAndStatus(Company company, String profileUid, AccountProfileStatus status);


    @Query("select d.route.profile from Drop d where d =:drop")
    Optional<AccountProfile> findByDrop(Drop drop);
}
