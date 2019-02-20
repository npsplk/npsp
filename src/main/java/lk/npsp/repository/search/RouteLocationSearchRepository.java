package lk.npsp.repository.search;

import lk.npsp.domain.RouteLocation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the RouteLocation entity.
 */
public interface RouteLocationSearchRepository extends ElasticsearchRepository<RouteLocation, Long> {
}
