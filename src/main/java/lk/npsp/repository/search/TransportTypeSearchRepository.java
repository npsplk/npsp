package lk.npsp.repository.search;

import lk.npsp.domain.TransportType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TransportType entity.
 */
public interface TransportTypeSearchRepository extends ElasticsearchRepository<TransportType, Long> {
}
