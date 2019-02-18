package lk.npsp.repository.search;

import lk.npsp.domain.ParkingArea;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ParkingArea entity.
 */
public interface ParkingAreaSearchRepository extends ElasticsearchRepository<ParkingArea, Long> {
}
