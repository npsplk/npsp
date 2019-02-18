package lk.npsp.repository.search;

import lk.npsp.domain.VehicleFacility;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the VehicleFacility entity.
 */
public interface VehicleFacilitySearchRepository extends ElasticsearchRepository<VehicleFacility, Long> {
}
