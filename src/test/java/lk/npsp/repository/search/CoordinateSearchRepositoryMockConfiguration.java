package lk.npsp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of CoordinateSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CoordinateSearchRepositoryMockConfiguration {

    @MockBean
    private CoordinateSearchRepository mockCoordinateSearchRepository;

}
