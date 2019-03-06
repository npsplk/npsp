package lk.npsp.repository;

import lk.npsp.domain.Bay;
import lk.npsp.domain.ScheduleInstance;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Bay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BayRepository extends JpaRepository<Bay, Long> {
    @Query(value = "select bay from Bay bay where bay.bindingAddress=:ipAddress")
    Optional<Bay> findOneBayByIP(@Param("ipAddress")String ipAddress);
}
