package lk.npsp.repository;

import lk.npsp.domain.ScheduleTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the ScheduleTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {

    @Query(value = "select distinct schedule_template from ScheduleTemplate schedule_template left join fetch schedule_template.weekdays left join fetch schedule_template.vehicleFacilities",
        countQuery = "select count(distinct schedule_template) from ScheduleTemplate schedule_template")
    Page<ScheduleTemplate> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct schedule_template from ScheduleTemplate schedule_template left join fetch schedule_template.weekdays left join fetch schedule_template.vehicleFacilities")
    List<ScheduleTemplate> findAllWithEagerRelationships();

    @Query("select schedule_template from ScheduleTemplate schedule_template left join fetch schedule_template.weekdays left join fetch schedule_template.vehicleFacilities where schedule_template.id =:id")
    Optional<ScheduleTemplate> findOneWithEagerRelationships(@Param("id") Long id);

}
