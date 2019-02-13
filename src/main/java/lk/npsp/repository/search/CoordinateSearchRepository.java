package lk.npsp.repository.search;

import lk.npsp.domain.Coordinate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Coordinate entity.
 */
public interface CoordinateSearchRepository extends ElasticsearchRepository<Coordinate, Long> {
}
