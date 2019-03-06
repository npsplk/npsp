package lk.npsp.repository;

import lk.npsp.domain.RouteLocation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the RouteLocation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RouteLocationRepository extends JpaRepository<RouteLocation, Long> {
    @Query(value = "select route_location from RouteLocation route_location " +
        "where route_location.route.id=:routeId order by route_location.sequenceNumber ASC")
    List<RouteLocation> findRouteLocationsByRoute(@Param("routeId") Long routeId);
}
