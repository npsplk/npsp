package lk.npsp.repository.search;

import lk.npsp.domain.Schedule;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Schedule entity.
 */
public interface ScheduleSearchRepository extends ElasticsearchRepository<Schedule, Long> {
}
