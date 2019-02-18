package lk.npsp.repository;

import lk.npsp.domain.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Schedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query(value = "select distinct schedule from Schedule schedule left join fetch schedule.weekdays left join fetch schedule.vehicleFacilities",
        countQuery = "select count(distinct schedule) from Schedule schedule")
    Page<Schedule> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct schedule from Schedule schedule left join fetch schedule.weekdays left join fetch schedule.vehicleFacilities")
    List<Schedule> findAllWithEagerRelationships();

    @Query("select schedule from Schedule schedule left join fetch schedule.weekdays left join fetch schedule.vehicleFacilities where schedule.id =:id")
    Optional<Schedule> findOneWithEagerRelationships(@Param("id") Long id);

}
