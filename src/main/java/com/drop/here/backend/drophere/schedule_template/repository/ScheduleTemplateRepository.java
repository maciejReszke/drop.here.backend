package com.drop.here.backend.drophere.schedule_template.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    Optional<ScheduleTemplate> findByIdAndCompany(Long scheduleTemplateId, Company company);

    List<ScheduleTemplate> findByCompany(Company company);
}
