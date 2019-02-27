package lk.npsp.repository.search;

import lk.npsp.domain.ScheduleInstance;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ScheduleInstance entity.
 */
public interface ScheduleInstanceSearchRepository extends ElasticsearchRepository<ScheduleInstance, Long> {
}
