package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.Coordinate;
import lk.npsp.repository.CoordinateRepository;
import lk.npsp.repository.search.CoordinateSearchRepository;
import lk.npsp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static lk.npsp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CoordinateResource REST controller.
 *
 * @see CoordinateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class CoordinateResourceIntTest {

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    @Autowired
    private CoordinateRepository coordinateRepository;

    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.CoordinateSearchRepositoryMockConfiguration
     */
    @Autowired
    private CoordinateSearchRepository mockCoordinateSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCoordinateMockMvc;

    private Coordinate coordinate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CoordinateResource coordinateResource = new CoordinateResource(coordinateRepository, mockCoordinateSearchRepository);
        this.restCoordinateMockMvc = MockMvcBuilders.standaloneSetup(coordinateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coordinate createEntity(EntityManager em) {
        Coordinate coordinate = new Coordinate()
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE);
        return coordinate;
    }

    @Before
    public void initTest() {
        coordinate = createEntity(em);
    }

    @Test
    @Transactional
    public void createCoordinate() throws Exception {
        int databaseSizeBeforeCreate = coordinateRepository.findAll().size();

        // Create the Coordinate
        restCoordinateMockMvc.perform(post("/api/coordinates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coordinate)))
            .andExpect(status().isCreated());

        // Validate the Coordinate in the database
        List<Coordinate> coordinateList = coordinateRepository.findAll();
        assertThat(coordinateList).hasSize(databaseSizeBeforeCreate + 1);
        Coordinate testCoordinate = coordinateList.get(coordinateList.size() - 1);
        assertThat(testCoordinate.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testCoordinate.getLatitude()).isEqualTo(DEFAULT_LATITUDE);

        // Validate the Coordinate in Elasticsearch
        verify(mockCoordinateSearchRepository, times(1)).save(testCoordinate);
    }

    @Test
    @Transactional
    public void createCoordinateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = coordinateRepository.findAll().size();

        // Create the Coordinate with an existing ID
        coordinate.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoordinateMockMvc.perform(post("/api/coordinates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coordinate)))
            .andExpect(status().isBadRequest());

        // Validate the Coordinate in the database
        List<Coordinate> coordinateList = coordinateRepository.findAll();
        assertThat(coordinateList).hasSize(databaseSizeBeforeCreate);

        // Validate the Coordinate in Elasticsearch
        verify(mockCoordinateSearchRepository, times(0)).save(coordinate);
    }

    @Test
    @Transactional
    public void getAllCoordinates() throws Exception {
        // Initialize the database
        coordinateRepository.saveAndFlush(coordinate);

        // Get all the coordinateList
        restCoordinateMockMvc.perform(get("/api/coordinates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coordinate.getId().intValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getCoordinate() throws Exception {
        // Initialize the database
        coordinateRepository.saveAndFlush(coordinate);

        // Get the coordinate
        restCoordinateMockMvc.perform(get("/api/coordinates/{id}", coordinate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(coordinate.getId().intValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCoordinate() throws Exception {
        // Get the coordinate
        restCoordinateMockMvc.perform(get("/api/coordinates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCoordinate() throws Exception {
        // Initialize the database
        coordinateRepository.saveAndFlush(coordinate);

        int databaseSizeBeforeUpdate = coordinateRepository.findAll().size();

        // Update the coordinate
        Coordinate updatedCoordinate = coordinateRepository.findById(coordinate.getId()).get();
        // Disconnect from session so that the updates on updatedCoordinate are not directly saved in db
        em.detach(updatedCoordinate);
        updatedCoordinate
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE);

        restCoordinateMockMvc.perform(put("/api/coordinates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCoordinate)))
            .andExpect(status().isOk());

        // Validate the Coordinate in the database
        List<Coordinate> coordinateList = coordinateRepository.findAll();
        assertThat(coordinateList).hasSize(databaseSizeBeforeUpdate);
        Coordinate testCoordinate = coordinateList.get(coordinateList.size() - 1);
        assertThat(testCoordinate.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testCoordinate.getLatitude()).isEqualTo(UPDATED_LATITUDE);

        // Validate the Coordinate in Elasticsearch
        verify(mockCoordinateSearchRepository, times(1)).save(testCoordinate);
    }

    @Test
    @Transactional
    public void updateNonExistingCoordinate() throws Exception {
        int databaseSizeBeforeUpdate = coordinateRepository.findAll().size();

        // Create the Coordinate

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoordinateMockMvc.perform(put("/api/coordinates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coordinate)))
            .andExpect(status().isBadRequest());

        // Validate the Coordinate in the database
        List<Coordinate> coordinateList = coordinateRepository.findAll();
        assertThat(coordinateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Coordinate in Elasticsearch
        verify(mockCoordinateSearchRepository, times(0)).save(coordinate);
    }

    @Test
    @Transactional
    public void deleteCoordinate() throws Exception {
        // Initialize the database
        coordinateRepository.saveAndFlush(coordinate);

        int databaseSizeBeforeDelete = coordinateRepository.findAll().size();

        // Delete the coordinate
        restCoordinateMockMvc.perform(delete("/api/coordinates/{id}", coordinate.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Coordinate> coordinateList = coordinateRepository.findAll();
        assertThat(coordinateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Coordinate in Elasticsearch
        verify(mockCoordinateSearchRepository, times(1)).deleteById(coordinate.getId());
    }

    @Test
    @Transactional
    public void searchCoordinate() throws Exception {
        // Initialize the database
        coordinateRepository.saveAndFlush(coordinate);
        when(mockCoordinateSearchRepository.search(queryStringQuery("id:" + coordinate.getId())))
            .thenReturn(Collections.singletonList(coordinate));
        // Search the coordinate
        restCoordinateMockMvc.perform(get("/api/_search/coordinates?query=id:" + coordinate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coordinate.getId().intValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Coordinate.class);
        Coordinate coordinate1 = new Coordinate();
        coordinate1.setId(1L);
        Coordinate coordinate2 = new Coordinate();
        coordinate2.setId(coordinate1.getId());
        assertThat(coordinate1).isEqualTo(coordinate2);
        coordinate2.setId(2L);
        assertThat(coordinate1).isNotEqualTo(coordinate2);
        coordinate1.setId(null);
        assertThat(coordinate1).isNotEqualTo(coordinate2);
    }
}
