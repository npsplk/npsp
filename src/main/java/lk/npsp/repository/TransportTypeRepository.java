package lk.npsp.repository;

import lk.npsp.domain.TransportType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TransportType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransportTypeRepository extends JpaRepository<TransportType, Long> {

}
