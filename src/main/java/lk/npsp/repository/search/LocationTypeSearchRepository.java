package lk.npsp.repository.search;

import lk.npsp.domain.LocationType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the LocationType entity.
 */
public interface LocationTypeSearchRepository extends ElasticsearchRepository<LocationType, Long> {
}
