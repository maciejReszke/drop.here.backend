package com.drop.here.backend.drophere.schedule_template.repository;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateShortResponse;
import com.drop.here.backend.drophere.schedule_template.entity.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// TODO MONO:
@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    Optional<ScheduleTemplate> findByIdAndCompany(Long scheduleTemplateId, Company company);

    @Query("select new com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateShortResponse(st.id, st.name, size(st.scheduleTemplateProducts) ) " +
            "from ScheduleTemplate st where " +
            "st.company =:company")
    List<ScheduleTemplateShortResponse> findByCompany(Company company);

    @Modifying
    @Query("delete from ScheduleTemplateProduct stp " +
            "where stp.product =:product")
    void deleteScheduleTemplateProductByProduct(Product product);

    @Query("select st from ScheduleTemplate st " +
            "left join fetch st.scheduleTemplateProducts where " +
            "st.id = :scheduleTemplateId and " +
            "st.company = :company")
    Optional<ScheduleTemplate> findByIdAndCompanyWithScheduleTemplateProducts(Long scheduleTemplateId, Company company);
}
