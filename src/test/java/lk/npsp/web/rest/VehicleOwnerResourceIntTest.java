package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.VehicleOwner;
import lk.npsp.repository.VehicleOwnerRepository;
import lk.npsp.repository.search.VehicleOwnerSearchRepository;
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
 * Test class for the VehicleOwnerResource REST controller.
 *
 * @see VehicleOwnerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class VehicleOwnerResourceIntTest {

    private static final String DEFAULT_OWNER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    @Autowired
    private VehicleOwnerRepository vehicleOwnerRepository;


    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.VehicleOwnerSearchRepositoryMockConfiguration
     */
    @Autowired
    private VehicleOwnerSearchRepository mockVehicleOwnerSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restVehicleOwnerMockMvc;

    private VehicleOwner vehicleOwner;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VehicleOwnerResource vehicleOwnerResource = new VehicleOwnerResource(vehicleOwnerRepository, mockVehicleOwnerSearchRepository);
        this.restVehicleOwnerMockMvc = MockMvcBuilders.standaloneSetup(vehicleOwnerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VehicleOwner createEntity(EntityManager em) {
        VehicleOwner vehicleOwner = new VehicleOwner()
            .ownerName(DEFAULT_OWNER_NAME)
            .contactNumber(DEFAULT_CONTACT_NUMBER)
            .address(DEFAULT_ADDRESS);
        return vehicleOwner;
    }

    @Before
    public void initTest() {
        vehicleOwner = createEntity(em);
    }

    @Test
    @Transactional
    public void createVehicleOwner() throws Exception {
        int databaseSizeBeforeCreate = vehicleOwnerRepository.findAll().size();

        // Create the VehicleOwner
        restVehicleOwnerMockMvc.perform(post("/api/vehicle-owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleOwner)))
            .andExpect(status().isCreated());

        // Validate the VehicleOwner in the database
        List<VehicleOwner> vehicleOwnerList = vehicleOwnerRepository.findAll();
        assertThat(vehicleOwnerList).hasSize(databaseSizeBeforeCreate + 1);
        VehicleOwner testVehicleOwner = vehicleOwnerList.get(vehicleOwnerList.size() - 1);
        assertThat(testVehicleOwner.getOwnerName()).isEqualTo(DEFAULT_OWNER_NAME);
        assertThat(testVehicleOwner.getContactNumber()).isEqualTo(DEFAULT_CONTACT_NUMBER);
        assertThat(testVehicleOwner.getAddress()).isEqualTo(DEFAULT_ADDRESS);

        // Validate the VehicleOwner in Elasticsearch
        verify(mockVehicleOwnerSearchRepository, times(1)).save(testVehicleOwner);
    }

    @Test
    @Transactional
    public void createVehicleOwnerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vehicleOwnerRepository.findAll().size();

        // Create the VehicleOwner with an existing ID
        vehicleOwner.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVehicleOwnerMockMvc.perform(post("/api/vehicle-owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleOwner)))
            .andExpect(status().isBadRequest());

        // Validate the VehicleOwner in the database
        List<VehicleOwner> vehicleOwnerList = vehicleOwnerRepository.findAll();
        assertThat(vehicleOwnerList).hasSize(databaseSizeBeforeCreate);

        // Validate the VehicleOwner in Elasticsearch
        verify(mockVehicleOwnerSearchRepository, times(0)).save(vehicleOwner);
    }

    @Test
    @Transactional
    public void checkOwnerNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vehicleOwnerRepository.findAll().size();
        // set the field null
        vehicleOwner.setOwnerName(null);

        // Create the VehicleOwner, which fails.

        restVehicleOwnerMockMvc.perform(post("/api/vehicle-owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleOwner)))
            .andExpect(status().isBadRequest());

        List<VehicleOwner> vehicleOwnerList = vehicleOwnerRepository.findAll();
        assertThat(vehicleOwnerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContactNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = vehicleOwnerRepository.findAll().size();
        // set the field null
        vehicleOwner.setContactNumber(null);

        // Create the VehicleOwner, which fails.

        restVehicleOwnerMockMvc.perform(post("/api/vehicle-owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleOwner)))
            .andExpect(status().isBadRequest());

        List<VehicleOwner> vehicleOwnerList = vehicleOwnerRepository.findAll();
        assertThat(vehicleOwnerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVehicleOwners() throws Exception {
        // Initialize the database
        vehicleOwnerRepository.saveAndFlush(vehicleOwner);

        // Get all the vehicleOwnerList
        restVehicleOwnerMockMvc.perform(get("/api/vehicle-owners?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehicleOwner.getId().intValue())))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME.toString())))
            .andExpect(jsonPath("$.[*].contactNumber").value(hasItem(DEFAULT_CONTACT_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())));
    }
    

    @Test
    @Transactional
    public void getVehicleOwner() throws Exception {
        // Initialize the database
        vehicleOwnerRepository.saveAndFlush(vehicleOwner);

        // Get the vehicleOwner
        restVehicleOwnerMockMvc.perform(get("/api/vehicle-owners/{id}", vehicleOwner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vehicleOwner.getId().intValue()))
            .andExpect(jsonPath("$.ownerName").value(DEFAULT_OWNER_NAME.toString()))
            .andExpect(jsonPath("$.contactNumber").value(DEFAULT_CONTACT_NUMBER.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingVehicleOwner() throws Exception {
        // Get the vehicleOwner
        restVehicleOwnerMockMvc.perform(get("/api/vehicle-owners/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVehicleOwner() throws Exception {
        // Initialize the database
        vehicleOwnerRepository.saveAndFlush(vehicleOwner);

        int databaseSizeBeforeUpdate = vehicleOwnerRepository.findAll().size();

        // Update the vehicleOwner
        VehicleOwner updatedVehicleOwner = vehicleOwnerRepository.findById(vehicleOwner.getId()).get();
        // Disconnect from session so that the updates on updatedVehicleOwner are not directly saved in db
        em.detach(updatedVehicleOwner);
        updatedVehicleOwner
            .ownerName(UPDATED_OWNER_NAME)
            .contactNumber(UPDATED_CONTACT_NUMBER)
            .address(UPDATED_ADDRESS);

        restVehicleOwnerMockMvc.perform(put("/api/vehicle-owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVehicleOwner)))
            .andExpect(status().isOk());

        // Validate the VehicleOwner in the database
        List<VehicleOwner> vehicleOwnerList = vehicleOwnerRepository.findAll();
        assertThat(vehicleOwnerList).hasSize(databaseSizeBeforeUpdate);
        VehicleOwner testVehicleOwner = vehicleOwnerList.get(vehicleOwnerList.size() - 1);
        assertThat(testVehicleOwner.getOwnerName()).isEqualTo(UPDATED_OWNER_NAME);
        assertThat(testVehicleOwner.getContactNumber()).isEqualTo(UPDATED_CONTACT_NUMBER);
        assertThat(testVehicleOwner.getAddress()).isEqualTo(UPDATED_ADDRESS);

        // Validate the VehicleOwner in Elasticsearch
        verify(mockVehicleOwnerSearchRepository, times(1)).save(testVehicleOwner);
    }

    @Test
    @Transactional
    public void updateNonExistingVehicleOwner() throws Exception {
        int databaseSizeBeforeUpdate = vehicleOwnerRepository.findAll().size();

        // Create the VehicleOwner

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restVehicleOwnerMockMvc.perform(put("/api/vehicle-owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleOwner)))
            .andExpect(status().isBadRequest());

        // Validate the VehicleOwner in the database
        List<VehicleOwner> vehicleOwnerList = vehicleOwnerRepository.findAll();
        assertThat(vehicleOwnerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the VehicleOwner in Elasticsearch
        verify(mockVehicleOwnerSearchRepository, times(0)).save(vehicleOwner);
    }

    @Test
    @Transactional
    public void deleteVehicleOwner() throws Exception {
        // Initialize the database
        vehicleOwnerRepository.saveAndFlush(vehicleOwner);

        int databaseSizeBeforeDelete = vehicleOwnerRepository.findAll().size();

        // Get the vehicleOwner
        restVehicleOwnerMockMvc.perform(delete("/api/vehicle-owners/{id}", vehicleOwner.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<VehicleOwner> vehicleOwnerList = vehicleOwnerRepository.findAll();
        assertThat(vehicleOwnerList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the VehicleOwner in Elasticsearch
        verify(mockVehicleOwnerSearchRepository, times(1)).deleteById(vehicleOwner.getId());
    }

    @Test
    @Transactional
    public void searchVehicleOwner() throws Exception {
        // Initialize the database
        vehicleOwnerRepository.saveAndFlush(vehicleOwner);
        when(mockVehicleOwnerSearchRepository.search(queryStringQuery("id:" + vehicleOwner.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(vehicleOwner), PageRequest.of(0, 1), 1));
        // Search the vehicleOwner
        restVehicleOwnerMockMvc.perform(get("/api/_search/vehicle-owners?query=id:" + vehicleOwner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehicleOwner.getId().intValue())))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME.toString())))
            .andExpect(jsonPath("$.[*].contactNumber").value(hasItem(DEFAULT_CONTACT_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VehicleOwner.class);
        VehicleOwner vehicleOwner1 = new VehicleOwner();
        vehicleOwner1.setId(1L);
        VehicleOwner vehicleOwner2 = new VehicleOwner();
        vehicleOwner2.setId(vehicleOwner1.getId());
        assertThat(vehicleOwner1).isEqualTo(vehicleOwner2);
        vehicleOwner2.setId(2L);
        assertThat(vehicleOwner1).isNotEqualTo(vehicleOwner2);
        vehicleOwner1.setId(null);
        assertThat(vehicleOwner1).isNotEqualTo(vehicleOwner2);
    }
}
