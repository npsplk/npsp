package lk.npsp.repository;

import lk.npsp.domain.ScheduleInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


/**
 * Spring Data  repository for the ScheduleInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleInstanceRepository extends JpaRepository<ScheduleInstance, Long> {
    @Query(value = "select distinct schedule_instance from ScheduleInstance schedule_instance where schedule_instance.date= :date",
        countQuery = "select count(distinct schedule_instance) from ScheduleInstance schedule_instance where schedule_instance.date= :date")
    Page<ScheduleInstance> findScheduleInstancesByDate(Pageable pageable, @Param("date") LocalDate date);
}
