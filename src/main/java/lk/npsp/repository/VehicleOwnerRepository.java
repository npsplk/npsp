package lk.npsp.repository;

import lk.npsp.domain.VehicleOwner;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the VehicleOwner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, Long> {

}
