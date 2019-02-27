package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.VehicleFacility;
import lk.npsp.repository.VehicleFacilityRepository;
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
import java.util.List;


import static lk.npsp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the VehicleFacilityResource REST controller.
 *
 * @see VehicleFacilityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class VehicleFacilityResourceIntTest {

    private static final String DEFAULT_FACILITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FACILITY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FACILITY_META = "AAAAAAAAAA";
    private static final String UPDATED_FACILITY_META = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private VehicleFacilityRepository vehicleFacilityRepository;

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

    private MockMvc restVehicleFacilityMockMvc;

    private VehicleFacility vehicleFacility;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VehicleFacilityResource vehicleFacilityResource = new VehicleFacilityResource(vehicleFacilityRepository);
        this.restVehicleFacilityMockMvc = MockMvcBuilders.standaloneSetup(vehicleFacilityResource)
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
    public static VehicleFacility createEntity(EntityManager em) {
        VehicleFacility vehicleFacility = new VehicleFacility()
            .facilityName(DEFAULT_FACILITY_NAME)
            .facilityMeta(DEFAULT_FACILITY_META)
            .description(DEFAULT_DESCRIPTION);
        return vehicleFacility;
    }

    @Before
    public void initTest() {
        vehicleFacility = createEntity(em);
    }

    @Test
    @Transactional
    public void createVehicleFacility() throws Exception {
        int databaseSizeBeforeCreate = vehicleFacilityRepository.findAll().size();

        // Create the VehicleFacility
        restVehicleFacilityMockMvc.perform(post("/api/vehicle-facilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleFacility)))
            .andExpect(status().isCreated());

        // Validate the VehicleFacility in the database
        List<VehicleFacility> vehicleFacilityList = vehicleFacilityRepository.findAll();
        assertThat(vehicleFacilityList).hasSize(databaseSizeBeforeCreate + 1);
        VehicleFacility testVehicleFacility = vehicleFacilityList.get(vehicleFacilityList.size() - 1);
        assertThat(testVehicleFacility.getFacilityName()).isEqualTo(DEFAULT_FACILITY_NAME);
        assertThat(testVehicleFacility.getFacilityMeta()).isEqualTo(DEFAULT_FACILITY_META);
        assertThat(testVehicleFacility.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createVehicleFacilityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = vehicleFacilityRepository.findAll().size();

        // Create the VehicleFacility with an existing ID
        vehicleFacility.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVehicleFacilityMockMvc.perform(post("/api/vehicle-facilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleFacility)))
            .andExpect(status().isBadRequest());

        // Validate the VehicleFacility in the database
        List<VehicleFacility> vehicleFacilityList = vehicleFacilityRepository.findAll();
        assertThat(vehicleFacilityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkFacilityNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vehicleFacilityRepository.findAll().size();
        // set the field null
        vehicleFacility.setFacilityName(null);

        // Create the VehicleFacility, which fails.

        restVehicleFacilityMockMvc.perform(post("/api/vehicle-facilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleFacility)))
            .andExpect(status().isBadRequest());

        List<VehicleFacility> vehicleFacilityList = vehicleFacilityRepository.findAll();
        assertThat(vehicleFacilityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFacilityMetaIsRequired() throws Exception {
        int databaseSizeBeforeTest = vehicleFacilityRepository.findAll().size();
        // set the field null
        vehicleFacility.setFacilityMeta(null);

        // Create the VehicleFacility, which fails.

        restVehicleFacilityMockMvc.perform(post("/api/vehicle-facilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleFacility)))
            .andExpect(status().isBadRequest());

        List<VehicleFacility> vehicleFacilityList = vehicleFacilityRepository.findAll();
        assertThat(vehicleFacilityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVehicleFacilities() throws Exception {
        // Initialize the database
        vehicleFacilityRepository.saveAndFlush(vehicleFacility);

        // Get all the vehicleFacilityList
        restVehicleFacilityMockMvc.perform(get("/api/vehicle-facilities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehicleFacility.getId().intValue())))
            .andExpect(jsonPath("$.[*].facilityName").value(hasItem(DEFAULT_FACILITY_NAME.toString())))
            .andExpect(jsonPath("$.[*].facilityMeta").value(hasItem(DEFAULT_FACILITY_META.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getVehicleFacility() throws Exception {
        // Initialize the database
        vehicleFacilityRepository.saveAndFlush(vehicleFacility);

        // Get the vehicleFacility
        restVehicleFacilityMockMvc.perform(get("/api/vehicle-facilities/{id}", vehicleFacility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vehicleFacility.getId().intValue()))
            .andExpect(jsonPath("$.facilityName").value(DEFAULT_FACILITY_NAME.toString()))
            .andExpect(jsonPath("$.facilityMeta").value(DEFAULT_FACILITY_META.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingVehicleFacility() throws Exception {
        // Get the vehicleFacility
        restVehicleFacilityMockMvc.perform(get("/api/vehicle-facilities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVehicleFacility() throws Exception {
        // Initialize the database
        vehicleFacilityRepository.saveAndFlush(vehicleFacility);

        int databaseSizeBeforeUpdate = vehicleFacilityRepository.findAll().size();

        // Update the vehicleFacility
        VehicleFacility updatedVehicleFacility = vehicleFacilityRepository.findById(vehicleFacility.getId()).get();
        // Disconnect from session so that the updates on updatedVehicleFacility are not directly saved in db
        em.detach(updatedVehicleFacility);
        updatedVehicleFacility
            .facilityName(UPDATED_FACILITY_NAME)
            .facilityMeta(UPDATED_FACILITY_META)
            .description(UPDATED_DESCRIPTION);

        restVehicleFacilityMockMvc.perform(put("/api/vehicle-facilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVehicleFacility)))
            .andExpect(status().isOk());

        // Validate the VehicleFacility in the database
        List<VehicleFacility> vehicleFacilityList = vehicleFacilityRepository.findAll();
        assertThat(vehicleFacilityList).hasSize(databaseSizeBeforeUpdate);
        VehicleFacility testVehicleFacility = vehicleFacilityList.get(vehicleFacilityList.size() - 1);
        assertThat(testVehicleFacility.getFacilityName()).isEqualTo(UPDATED_FACILITY_NAME);
        assertThat(testVehicleFacility.getFacilityMeta()).isEqualTo(UPDATED_FACILITY_META);
        assertThat(testVehicleFacility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingVehicleFacility() throws Exception {
        int databaseSizeBeforeUpdate = vehicleFacilityRepository.findAll().size();

        // Create the VehicleFacility

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehicleFacilityMockMvc.perform(put("/api/vehicle-facilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vehicleFacility)))
            .andExpect(status().isBadRequest());

        // Validate the VehicleFacility in the database
        List<VehicleFacility> vehicleFacilityList = vehicleFacilityRepository.findAll();
        assertThat(vehicleFacilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteVehicleFacility() throws Exception {
        // Initialize the database
        vehicleFacilityRepository.saveAndFlush(vehicleFacility);

        int databaseSizeBeforeDelete = vehicleFacilityRepository.findAll().size();

        // Delete the vehicleFacility
        restVehicleFacilityMockMvc.perform(delete("/api/vehicle-facilities/{id}", vehicleFacility.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<VehicleFacility> vehicleFacilityList = vehicleFacilityRepository.findAll();
        assertThat(vehicleFacilityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VehicleFacility.class);
        VehicleFacility vehicleFacility1 = new VehicleFacility();
        vehicleFacility1.setId(1L);
        VehicleFacility vehicleFacility2 = new VehicleFacility();
        vehicleFacility2.setId(vehicleFacility1.getId());
        assertThat(vehicleFacility1).isEqualTo(vehicleFacility2);
        vehicleFacility2.setId(2L);
        assertThat(vehicleFacility1).isNotEqualTo(vehicleFacility2);
        vehicleFacility1.setId(null);
        assertThat(vehicleFacility1).isNotEqualTo(vehicleFacility2);
    }
}
