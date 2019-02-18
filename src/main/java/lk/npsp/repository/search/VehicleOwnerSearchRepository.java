package lk.npsp.repository.search;

import lk.npsp.domain.VehicleOwner;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the VehicleOwner entity.
 */
public interface VehicleOwnerSearchRepository extends ElasticsearchRepository<VehicleOwner, Long> {
}
