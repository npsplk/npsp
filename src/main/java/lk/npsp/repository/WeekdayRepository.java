package lk.npsp.repository;

import lk.npsp.domain.Weekday;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Weekday entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WeekdayRepository extends JpaRepository<Weekday, Long> {

}
