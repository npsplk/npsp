package lk.npsp.repository;

import lk.npsp.domain.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Vehicle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query(value = "select distinct vehicle from Vehicle vehicle left join fetch vehicle.vehicleFacilities",
        countQuery = "select count(distinct vehicle) from Vehicle vehicle")
    Page<Vehicle> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct vehicle from Vehicle vehicle left join fetch vehicle.vehicleFacilities")
    List<Vehicle> findAllWithEagerRelationships();

    @Query("select vehicle from Vehicle vehicle left join fetch vehicle.vehicleFacilities where vehicle.id =:id")
    Optional<Vehicle> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select vehicle from Vehicle vehicle where vehicle.registrationNumber =:number")
    Optional<Vehicle> findOneByRegistrationNumber(@Param("number") String number);

}
