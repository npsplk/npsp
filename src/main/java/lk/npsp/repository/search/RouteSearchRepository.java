package lk.npsp.repository.search;

import lk.npsp.domain.Route;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Route entity.
 */
public interface RouteSearchRepository extends ElasticsearchRepository<Route, Long> {
}
