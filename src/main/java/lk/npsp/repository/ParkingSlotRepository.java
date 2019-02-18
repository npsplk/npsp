package lk.npsp.repository;

import lk.npsp.domain.ParkingSlot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ParkingSlot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

}
