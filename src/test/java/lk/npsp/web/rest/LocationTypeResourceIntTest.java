package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.LocationType;
import lk.npsp.repository.LocationTypeRepository;
import lk.npsp.repository.search.LocationTypeSearchRepository;
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
 * Test class for the LocationTypeResource REST controller.
 *
 * @see LocationTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class LocationTypeResourceIntTest {

    private static final String DEFAULT_TYPE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_META_CODE = "AAAAAAAAAA";
    private static final String UPDATED_META_CODE = "BBBBBBBBBB";

    @Autowired
    private LocationTypeRepository locationTypeRepository;

    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.LocationTypeSearchRepositoryMockConfiguration
     */
    @Autowired
    private LocationTypeSearchRepository mockLocationTypeSearchRepository;

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

    private MockMvc restLocationTypeMockMvc;

    private LocationType locationType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LocationTypeResource locationTypeResource = new LocationTypeResource(locationTypeRepository, mockLocationTypeSearchRepository);
        this.restLocationTypeMockMvc = MockMvcBuilders.standaloneSetup(locationTypeResource)
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
    public static LocationType createEntity(EntityManager em) {
        LocationType locationType = new LocationType()
            .typeName(DEFAULT_TYPE_NAME)
            .metaCode(DEFAULT_META_CODE);
        return locationType;
    }

    @Before
    public void initTest() {
        locationType = createEntity(em);
    }

    @Test
    @Transactional
    public void createLocationType() throws Exception {
        int databaseSizeBeforeCreate = locationTypeRepository.findAll().size();

        // Create the LocationType
        restLocationTypeMockMvc.perform(post("/api/location-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locationType)))
            .andExpect(status().isCreated());

        // Validate the LocationType in the database
        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        assertThat(locationTypeList).hasSize(databaseSizeBeforeCreate + 1);
        LocationType testLocationType = locationTypeList.get(locationTypeList.size() - 1);
        assertThat(testLocationType.getTypeName()).isEqualTo(DEFAULT_TYPE_NAME);
        assertThat(testLocationType.getMetaCode()).isEqualTo(DEFAULT_META_CODE);

        // Validate the LocationType in Elasticsearch
        verify(mockLocationTypeSearchRepository, times(1)).save(testLocationType);
    }

    @Test
    @Transactional
    public void createLocationTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = locationTypeRepository.findAll().size();

        // Create the LocationType with an existing ID
        locationType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocationTypeMockMvc.perform(post("/api/location-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locationType)))
            .andExpect(status().isBadRequest());

        // Validate the LocationType in the database
        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        assertThat(locationTypeList).hasSize(databaseSizeBeforeCreate);

        // Validate the LocationType in Elasticsearch
        verify(mockLocationTypeSearchRepository, times(0)).save(locationType);
    }

    @Test
    @Transactional
    public void checkTypeNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = locationTypeRepository.findAll().size();
        // set the field null
        locationType.setTypeName(null);

        // Create the LocationType, which fails.

        restLocationTypeMockMvc.perform(post("/api/location-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locationType)))
            .andExpect(status().isBadRequest());

        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        assertThat(locationTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMetaCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = locationTypeRepository.findAll().size();
        // set the field null
        locationType.setMetaCode(null);

        // Create the LocationType, which fails.

        restLocationTypeMockMvc.perform(post("/api/location-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locationType)))
            .andExpect(status().isBadRequest());

        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        assertThat(locationTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLocationTypes() throws Exception {
        // Initialize the database
        locationTypeRepository.saveAndFlush(locationType);

        // Get all the locationTypeList
        restLocationTypeMockMvc.perform(get("/api/location-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locationType.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME.toString())))
            .andExpect(jsonPath("$.[*].metaCode").value(hasItem(DEFAULT_META_CODE.toString())));
    }
    
    @Test
    @Transactional
    public void getLocationType() throws Exception {
        // Initialize the database
        locationTypeRepository.saveAndFlush(locationType);

        // Get the locationType
        restLocationTypeMockMvc.perform(get("/api/location-types/{id}", locationType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(locationType.getId().intValue()))
            .andExpect(jsonPath("$.typeName").value(DEFAULT_TYPE_NAME.toString()))
            .andExpect(jsonPath("$.metaCode").value(DEFAULT_META_CODE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLocationType() throws Exception {
        // Get the locationType
        restLocationTypeMockMvc.perform(get("/api/location-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLocationType() throws Exception {
        // Initialize the database
        locationTypeRepository.saveAndFlush(locationType);

        int databaseSizeBeforeUpdate = locationTypeRepository.findAll().size();

        // Update the locationType
        LocationType updatedLocationType = locationTypeRepository.findById(locationType.getId()).get();
        // Disconnect from session so that the updates on updatedLocationType are not directly saved in db
        em.detach(updatedLocationType);
        updatedLocationType
            .typeName(UPDATED_TYPE_NAME)
            .metaCode(UPDATED_META_CODE);

        restLocationTypeMockMvc.perform(put("/api/location-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLocationType)))
            .andExpect(status().isOk());

        // Validate the LocationType in the database
        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        assertThat(locationTypeList).hasSize(databaseSizeBeforeUpdate);
        LocationType testLocationType = locationTypeList.get(locationTypeList.size() - 1);
        assertThat(testLocationType.getTypeName()).isEqualTo(UPDATED_TYPE_NAME);
        assertThat(testLocationType.getMetaCode()).isEqualTo(UPDATED_META_CODE);

        // Validate the LocationType in Elasticsearch
        verify(mockLocationTypeSearchRepository, times(1)).save(testLocationType);
    }

    @Test
    @Transactional
    public void updateNonExistingLocationType() throws Exception {
        int databaseSizeBeforeUpdate = locationTypeRepository.findAll().size();

        // Create the LocationType

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocationTypeMockMvc.perform(put("/api/location-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locationType)))
            .andExpect(status().isBadRequest());

        // Validate the LocationType in the database
        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        assertThat(locationTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LocationType in Elasticsearch
        verify(mockLocationTypeSearchRepository, times(0)).save(locationType);
    }

    @Test
    @Transactional
    public void deleteLocationType() throws Exception {
        // Initialize the database
        locationTypeRepository.saveAndFlush(locationType);

        int databaseSizeBeforeDelete = locationTypeRepository.findAll().size();

        // Delete the locationType
        restLocationTypeMockMvc.perform(delete("/api/location-types/{id}", locationType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<LocationType> locationTypeList = locationTypeRepository.findAll();
        assertThat(locationTypeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LocationType in Elasticsearch
        verify(mockLocationTypeSearchRepository, times(1)).deleteById(locationType.getId());
    }

    @Test
    @Transactional
    public void searchLocationType() throws Exception {
        // Initialize the database
        locationTypeRepository.saveAndFlush(locationType);
        when(mockLocationTypeSearchRepository.search(queryStringQuery("id:" + locationType.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(locationType), PageRequest.of(0, 1), 1));
        // Search the locationType
        restLocationTypeMockMvc.perform(get("/api/_search/location-types?query=id:" + locationType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locationType.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME)))
            .andExpect(jsonPath("$.[*].metaCode").value(hasItem(DEFAULT_META_CODE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocationType.class);
        LocationType locationType1 = new LocationType();
        locationType1.setId(1L);
        LocationType locationType2 = new LocationType();
        locationType2.setId(locationType1.getId());
        assertThat(locationType1).isEqualTo(locationType2);
        locationType2.setId(2L);
        assertThat(locationType1).isNotEqualTo(locationType2);
        locationType1.setId(null);
        assertThat(locationType1).isNotEqualTo(locationType2);
    }
}
