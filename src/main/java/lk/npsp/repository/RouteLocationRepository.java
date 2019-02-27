package lk.npsp.repository;

import lk.npsp.domain.RouteLocation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the RouteLocation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RouteLocationRepository extends JpaRepository<RouteLocation, Long> {

}
