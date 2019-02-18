package lk.npsp.repository.search;

import lk.npsp.domain.Trip;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Trip entity.
 */
public interface TripSearchRepository extends ElasticsearchRepository<Trip, Long> {
}
