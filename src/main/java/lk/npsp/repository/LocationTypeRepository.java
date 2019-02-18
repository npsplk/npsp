package lk.npsp.repository;

import lk.npsp.domain.LocationType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the LocationType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocationTypeRepository extends JpaRepository<LocationType, Long> {

}
