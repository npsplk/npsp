package lk.npsp.repository.search;

import lk.npsp.domain.Weekday;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Weekday entity.
 */
public interface WeekdaySearchRepository extends ElasticsearchRepository<Weekday, Long> {
}
