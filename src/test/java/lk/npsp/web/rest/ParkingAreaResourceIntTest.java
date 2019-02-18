package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.ParkingArea;
import lk.npsp.repository.ParkingAreaRepository;
import lk.npsp.repository.search.ParkingAreaSearchRepository;
import lk.npsp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
 * Test class for the ParkingAreaResource REST controller.
 *
 * @see ParkingAreaResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class ParkingAreaResourceIntTest {

    private static final String DEFAULT_AREA_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AREA_NAME = "BBBBBBBBBB";

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.ParkingAreaSearchRepositoryMockConfiguration
     */
    @Autowired
    private ParkingAreaSearchRepository mockParkingAreaSearchRepository;

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

    private MockMvc restParkingAreaMockMvc;

    private ParkingArea parkingArea;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ParkingAreaResource parkingAreaResource = new ParkingAreaResource(parkingAreaRepository, mockParkingAreaSearchRepository);
        this.restParkingAreaMockMvc = MockMvcBuilders.standaloneSetup(parkingAreaResource)
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
    public static ParkingArea createEntity(EntityManager em) {
        ParkingArea parkingArea = new ParkingArea()
            .areaName(DEFAULT_AREA_NAME);
        return parkingArea;
    }

    @Before
    public void initTest() {
        parkingArea = createEntity(em);
    }

    @Test
    @Transactional
    public void createParkingArea() throws Exception {
        int databaseSizeBeforeCreate = parkingAreaRepository.findAll().size();

        // Create the ParkingArea
        restParkingAreaMockMvc.perform(post("/api/parking-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(parkingArea)))
            .andExpect(status().isCreated());

        // Validate the ParkingArea in the database
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findAll();
        assertThat(parkingAreaList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingArea testParkingArea = parkingAreaList.get(parkingAreaList.size() - 1);
        assertThat(testParkingArea.getAreaName()).isEqualTo(DEFAULT_AREA_NAME);

        // Validate the ParkingArea in Elasticsearch
        verify(mockParkingAreaSearchRepository, times(1)).save(testParkingArea);
    }

    @Test
    @Transactional
    public void createParkingAreaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = parkingAreaRepository.findAll().size();

        // Create the ParkingArea with an existing ID
        parkingArea.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restParkingAreaMockMvc.perform(post("/api/parking-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(parkingArea)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingArea in the database
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findAll();
        assertThat(parkingAreaList).hasSize(databaseSizeBeforeCreate);

        // Validate the ParkingArea in Elasticsearch
        verify(mockParkingAreaSearchRepository, times(0)).save(parkingArea);
    }

    @Test
    @Transactional
    public void checkAreaNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingAreaRepository.findAll().size();
        // set the field null
        parkingArea.setAreaName(null);

        // Create the ParkingArea, which fails.

        restParkingAreaMockMvc.perform(post("/api/parking-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(parkingArea)))
            .andExpect(status().isBadRequest());

        List<ParkingArea> parkingAreaList = parkingAreaRepository.findAll();
        assertThat(parkingAreaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParkingAreas() throws Exception {
        // Initialize the database
        parkingAreaRepository.saveAndFlush(parkingArea);

        // Get all the parkingAreaList
        restParkingAreaMockMvc.perform(get("/api/parking-areas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingArea.getId().intValue())))
            .andExpect(jsonPath("$.[*].areaName").value(hasItem(DEFAULT_AREA_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getParkingArea() throws Exception {
        // Initialize the database
        parkingAreaRepository.saveAndFlush(parkingArea);

        // Get the parkingArea
        restParkingAreaMockMvc.perform(get("/api/parking-areas/{id}", parkingArea.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(parkingArea.getId().intValue()))
            .andExpect(jsonPath("$.areaName").value(DEFAULT_AREA_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingParkingArea() throws Exception {
        // Get the parkingArea
        restParkingAreaMockMvc.perform(get("/api/parking-areas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParkingArea() throws Exception {
        // Initialize the database
        parkingAreaRepository.saveAndFlush(parkingArea);

        int databaseSizeBeforeUpdate = parkingAreaRepository.findAll().size();

        // Update the parkingArea
        ParkingArea updatedParkingArea = parkingAreaRepository.findById(parkingArea.getId()).get();
        // Disconnect from session so that the updates on updatedParkingArea are not directly saved in db
        em.detach(updatedParkingArea);
        updatedParkingArea
            .areaName(UPDATED_AREA_NAME);

        restParkingAreaMockMvc.perform(put("/api/parking-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedParkingArea)))
            .andExpect(status().isOk());

        // Validate the ParkingArea in the database
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findAll();
        assertThat(parkingAreaList).hasSize(databaseSizeBeforeUpdate);
        ParkingArea testParkingArea = parkingAreaList.get(parkingAreaList.size() - 1);
        assertThat(testParkingArea.getAreaName()).isEqualTo(UPDATED_AREA_NAME);

        // Validate the ParkingArea in Elasticsearch
        verify(mockParkingAreaSearchRepository, times(1)).save(testParkingArea);
    }

    @Test
    @Transactional
    public void updateNonExistingParkingArea() throws Exception {
        int databaseSizeBeforeUpdate = parkingAreaRepository.findAll().size();

        // Create the ParkingArea

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingAreaMockMvc.perform(put("/api/parking-areas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(parkingArea)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingArea in the database
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findAll();
        assertThat(parkingAreaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingArea in Elasticsearch
        verify(mockParkingAreaSearchRepository, times(0)).save(parkingArea);
    }

    @Test
    @Transactional
    public void deleteParkingArea() throws Exception {
        // Initialize the database
        parkingAreaRepository.saveAndFlush(parkingArea);

        int databaseSizeBeforeDelete = parkingAreaRepository.findAll().size();

        // Delete the parkingArea
        restParkingAreaMockMvc.perform(delete("/api/parking-areas/{id}", parkingArea.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ParkingArea> parkingAreaList = parkingAreaRepository.findAll();
        assertThat(parkingAreaList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ParkingArea in Elasticsearch
        verify(mockParkingAreaSearchRepository, times(1)).deleteById(parkingArea.getId());
    }

    @Test
    @Transactional
    public void searchParkingArea() throws Exception {
        // Initialize the database
        parkingAreaRepository.saveAndFlush(parkingArea);
        when(mockParkingAreaSearchRepository.search(queryStringQuery("id:" + parkingArea.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(parkingArea), PageRequest.of(0, 1), 1));
        // Search the parkingArea
        restParkingAreaMockMvc.perform(get("/api/_search/parking-areas?query=id:" + parkingArea.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingArea.getId().intValue())))
            .andExpect(jsonPath("$.[*].areaName").value(hasItem(DEFAULT_AREA_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParkingArea.class);
        ParkingArea parkingArea1 = new ParkingArea();
        parkingArea1.setId(1L);
        ParkingArea parkingArea2 = new ParkingArea();
        parkingArea2.setId(parkingArea1.getId());
        assertThat(parkingArea1).isEqualTo(parkingArea2);
        parkingArea2.setId(2L);
        assertThat(parkingArea1).isNotEqualTo(parkingArea2);
        parkingArea1.setId(null);
        assertThat(parkingArea1).isNotEqualTo(parkingArea2);
    }
}
