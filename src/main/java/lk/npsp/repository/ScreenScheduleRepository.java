package lk.npsp.repository;

import lk.npsp.domain.Bay;
import lk.npsp.domain.ScheduleInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


/**
 * Spring Data  repository for the ScheduleInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScreenScheduleRepository extends JpaRepository<ScheduleInstance, Long> {

    @Query(value = "select schedule_instance from ScheduleInstance schedule_instance " +
        "where schedule_instance.bay=:bay and schedule_instance.actualScheduledTime >:now " +
        "order by schedule_instance.actualScheduledTime ASC")
    List<ScheduleInstance> findScheduleInstancesByScreen(@Param("bay") Bay bay, @Param("now") Instant now);

}
