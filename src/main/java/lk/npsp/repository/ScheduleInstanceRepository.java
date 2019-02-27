package lk.npsp.repository;

import lk.npsp.domain.ScheduleInstance;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ScheduleInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleInstanceRepository extends JpaRepository<ScheduleInstance, Long> {

}
