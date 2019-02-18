package lk.npsp.repository.search;

import lk.npsp.domain.ParkingSlot;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ParkingSlot entity.
 */
public interface ParkingSlotSearchRepository extends ElasticsearchRepository<ParkingSlot, Long> {
}
