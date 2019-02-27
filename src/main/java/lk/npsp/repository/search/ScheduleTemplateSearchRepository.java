package lk.npsp.repository.search;

import lk.npsp.domain.ScheduleTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ScheduleTemplate entity.
 */
public interface ScheduleTemplateSearchRepository extends ElasticsearchRepository<ScheduleTemplate, Long> {
}
