package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.TransportType;
import lk.npsp.repository.TransportTypeRepository;
import lk.npsp.repository.search.TransportTypeSearchRepository;
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
 * Test class for the TransportTypeResource REST controller.
 *
 * @see TransportTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class TransportTypeResourceIntTest {

    private static final String DEFAULT_TYPE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_META_CODE = "AAAAAAAAAA";
    private static final String UPDATED_META_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private TransportTypeRepository transportTypeRepository;

    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.TransportTypeSearchRepositoryMockConfiguration
     */
    @Autowired
    private TransportTypeSearchRepository mockTransportTypeSearchRepository;

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

    private MockMvc restTransportTypeMockMvc;

    private TransportType transportType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TransportTypeResource transportTypeResource = new TransportTypeResource(transportTypeRepository, mockTransportTypeSearchRepository);
        this.restTransportTypeMockMvc = MockMvcBuilders.standaloneSetup(transportTypeResource)
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
    public static TransportType createEntity(EntityManager em) {
        TransportType transportType = new TransportType()
            .typeName(DEFAULT_TYPE_NAME)
            .metaCode(DEFAULT_META_CODE)
            .description(DEFAULT_DESCRIPTION);
        return transportType;
    }

    @Before
    public void initTest() {
        transportType = createEntity(em);
    }

    @Test
    @Transactional
    public void createTransportType() throws Exception {
        int databaseSizeBeforeCreate = transportTypeRepository.findAll().size();

        // Create the TransportType
        restTransportTypeMockMvc.perform(post("/api/transport-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transportType)))
            .andExpect(status().isCreated());

        // Validate the TransportType in the database
        List<TransportType> transportTypeList = transportTypeRepository.findAll();
        assertThat(transportTypeList).hasSize(databaseSizeBeforeCreate + 1);
        TransportType testTransportType = transportTypeList.get(transportTypeList.size() - 1);
        assertThat(testTransportType.getTypeName()).isEqualTo(DEFAULT_TYPE_NAME);
        assertThat(testTransportType.getMetaCode()).isEqualTo(DEFAULT_META_CODE);
        assertThat(testTransportType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the TransportType in Elasticsearch
        verify(mockTransportTypeSearchRepository, times(1)).save(testTransportType);
    }

    @Test
    @Transactional
    public void createTransportTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = transportTypeRepository.findAll().size();

        // Create the TransportType with an existing ID
        transportType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransportTypeMockMvc.perform(post("/api/transport-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transportType)))
            .andExpect(status().isBadRequest());

        // Validate the TransportType in the database
        List<TransportType> transportTypeList = transportTypeRepository.findAll();
        assertThat(transportTypeList).hasSize(databaseSizeBeforeCreate);

        // Validate the TransportType in Elasticsearch
        verify(mockTransportTypeSearchRepository, times(0)).save(transportType);
    }

    @Test
    @Transactional
    public void checkTypeNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transportTypeRepository.findAll().size();
        // set the field null
        transportType.setTypeName(null);

        // Create the TransportType, which fails.

        restTransportTypeMockMvc.perform(post("/api/transport-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transportType)))
            .andExpect(status().isBadRequest());

        List<TransportType> transportTypeList = transportTypeRepository.findAll();
        assertThat(transportTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMetaCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transportTypeRepository.findAll().size();
        // set the field null
        transportType.setMetaCode(null);

        // Create the TransportType, which fails.

        restTransportTypeMockMvc.perform(post("/api/transport-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transportType)))
            .andExpect(status().isBadRequest());

        List<TransportType> transportTypeList = transportTypeRepository.findAll();
        assertThat(transportTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTransportTypes() throws Exception {
        // Initialize the database
        transportTypeRepository.saveAndFlush(transportType);

        // Get all the transportTypeList
        restTransportTypeMockMvc.perform(get("/api/transport-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transportType.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME.toString())))
            .andExpect(jsonPath("$.[*].metaCode").value(hasItem(DEFAULT_META_CODE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getTransportType() throws Exception {
        // Initialize the database
        transportTypeRepository.saveAndFlush(transportType);

        // Get the transportType
        restTransportTypeMockMvc.perform(get("/api/transport-types/{id}", transportType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(transportType.getId().intValue()))
            .andExpect(jsonPath("$.typeName").value(DEFAULT_TYPE_NAME.toString()))
            .andExpect(jsonPath("$.metaCode").value(DEFAULT_META_CODE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTransportType() throws Exception {
        // Get the transportType
        restTransportTypeMockMvc.perform(get("/api/transport-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTransportType() throws Exception {
        // Initialize the database
        transportTypeRepository.saveAndFlush(transportType);

        int databaseSizeBeforeUpdate = transportTypeRepository.findAll().size();

        // Update the transportType
        TransportType updatedTransportType = transportTypeRepository.findById(transportType.getId()).get();
        // Disconnect from session so that the updates on updatedTransportType are not directly saved in db
        em.detach(updatedTransportType);
        updatedTransportType
            .typeName(UPDATED_TYPE_NAME)
            .metaCode(UPDATED_META_CODE)
            .description(UPDATED_DESCRIPTION);

        restTransportTypeMockMvc.perform(put("/api/transport-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTransportType)))
            .andExpect(status().isOk());

        // Validate the TransportType in the database
        List<TransportType> transportTypeList = transportTypeRepository.findAll();
        assertThat(transportTypeList).hasSize(databaseSizeBeforeUpdate);
        TransportType testTransportType = transportTypeList.get(transportTypeList.size() - 1);
        assertThat(testTransportType.getTypeName()).isEqualTo(UPDATED_TYPE_NAME);
        assertThat(testTransportType.getMetaCode()).isEqualTo(UPDATED_META_CODE);
        assertThat(testTransportType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the TransportType in Elasticsearch
        verify(mockTransportTypeSearchRepository, times(1)).save(testTransportType);
    }

    @Test
    @Transactional
    public void updateNonExistingTransportType() throws Exception {
        int databaseSizeBeforeUpdate = transportTypeRepository.findAll().size();

        // Create the TransportType

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransportTypeMockMvc.perform(put("/api/transport-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transportType)))
            .andExpect(status().isBadRequest());

        // Validate the TransportType in the database
        List<TransportType> transportTypeList = transportTypeRepository.findAll();
        assertThat(transportTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TransportType in Elasticsearch
        verify(mockTransportTypeSearchRepository, times(0)).save(transportType);
    }

    @Test
    @Transactional
    public void deleteTransportType() throws Exception {
        // Initialize the database
        transportTypeRepository.saveAndFlush(transportType);

        int databaseSizeBeforeDelete = transportTypeRepository.findAll().size();

        // Delete the transportType
        restTransportTypeMockMvc.perform(delete("/api/transport-types/{id}", transportType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TransportType> transportTypeList = transportTypeRepository.findAll();
        assertThat(transportTypeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TransportType in Elasticsearch
        verify(mockTransportTypeSearchRepository, times(1)).deleteById(transportType.getId());
    }

    @Test
    @Transactional
    public void searchTransportType() throws Exception {
        // Initialize the database
        transportTypeRepository.saveAndFlush(transportType);
        when(mockTransportTypeSearchRepository.search(queryStringQuery("id:" + transportType.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(transportType), PageRequest.of(0, 1), 1));
        // Search the transportType
        restTransportTypeMockMvc.perform(get("/api/_search/transport-types?query=id:" + transportType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transportType.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeName").value(hasItem(DEFAULT_TYPE_NAME)))
            .andExpect(jsonPath("$.[*].metaCode").value(hasItem(DEFAULT_META_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransportType.class);
        TransportType transportType1 = new TransportType();
        transportType1.setId(1L);
        TransportType transportType2 = new TransportType();
        transportType2.setId(transportType1.getId());
        assertThat(transportType1).isEqualTo(transportType2);
        transportType2.setId(2L);
        assertThat(transportType1).isNotEqualTo(transportType2);
        transportType1.setId(null);
        assertThat(transportType1).isNotEqualTo(transportType2);
    }
}
