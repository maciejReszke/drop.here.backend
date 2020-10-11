package com.drop.here.backend.drophere.notification.repository;

import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJobRepository extends JpaRepository<NotificationJob, Long> {

    @Query("select n from NotificationJob n")
    List<NotificationJob> findAllByNotificationIsNotNull(PageRequest pageable);

    @Modifying
    @Query("delete from NotificationJob n where " +
            "n in (:notifications)")
    void deleteByNotificationJobIn(List<NotificationJob> notifications);
}
