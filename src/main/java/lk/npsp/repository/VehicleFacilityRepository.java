package lk.npsp.repository;

import lk.npsp.domain.VehicleFacility;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the VehicleFacility entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleFacilityRepository extends JpaRepository<VehicleFacility, Long> {

}
