package lk.npsp.repository;

import lk.npsp.domain.Bay;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Bay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BayRepository extends JpaRepository<Bay, Long> {

}
