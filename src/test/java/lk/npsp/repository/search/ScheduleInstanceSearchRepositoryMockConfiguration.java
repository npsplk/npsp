package lk.npsp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of ScheduleInstanceSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ScheduleInstanceSearchRepositoryMockConfiguration {

    @MockBean
    private ScheduleInstanceSearchRepository mockScheduleInstanceSearchRepository;

}
