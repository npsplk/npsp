package lk.npsp.repository;

import lk.npsp.domain.ScheduleInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the ScheduleInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScreenScheduleRepository extends JpaRepository<ScheduleInstance, Long> {

    @Query(value = "select schedule_instance from ScheduleInstance schedule_instance order by schedule_instance.actualScheduledTime ASC")
    List<ScheduleInstance> findScheduleInstancesByScreen();

}
