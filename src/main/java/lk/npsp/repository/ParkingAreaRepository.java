package lk.npsp.repository;

import lk.npsp.domain.ParkingArea;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ParkingArea entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {

}
