package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.Bay;
import lk.npsp.repository.BayRepository;
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
 * Test class for the BayResource REST controller.
 *
 * @see BayResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class BayResourceIntTest {

    private static final String DEFAULT_BAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BINDING_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_BINDING_ADDRESS = "BBBBBBBBBB";

    @Autowired
    private BayRepository bayRepository;

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

    private MockMvc restBayMockMvc;

    private Bay bay;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BayResource bayResource = new BayResource(bayRepository);
        this.restBayMockMvc = MockMvcBuilders.standaloneSetup(bayResource)
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
    public static Bay createEntity(EntityManager em) {
        Bay bay = new Bay()
            .bayName(DEFAULT_BAY_NAME)
            .bindingAddress(DEFAULT_BINDING_ADDRESS);
        return bay;
    }

    @Before
    public void initTest() {
        bay = createEntity(em);
    }

    @Test
    @Transactional
    public void createBay() throws Exception {
        int databaseSizeBeforeCreate = bayRepository.findAll().size();

        // Create the Bay
        restBayMockMvc.perform(post("/api/bays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bay)))
            .andExpect(status().isCreated());

        // Validate the Bay in the database
        List<Bay> bayList = bayRepository.findAll();
        assertThat(bayList).hasSize(databaseSizeBeforeCreate + 1);
        Bay testBay = bayList.get(bayList.size() - 1);
        assertThat(testBay.getBayName()).isEqualTo(DEFAULT_BAY_NAME);
        assertThat(testBay.getBindingAddress()).isEqualTo(DEFAULT_BINDING_ADDRESS);
    }

    @Test
    @Transactional
    public void createBayWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bayRepository.findAll().size();

        // Create the Bay with an existing ID
        bay.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBayMockMvc.perform(post("/api/bays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bay)))
            .andExpect(status().isBadRequest());

        // Validate the Bay in the database
        List<Bay> bayList = bayRepository.findAll();
        assertThat(bayList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBays() throws Exception {
        // Initialize the database
        bayRepository.saveAndFlush(bay);

        // Get all the bayList
        restBayMockMvc.perform(get("/api/bays?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bay.getId().intValue())))
            .andExpect(jsonPath("$.[*].bayName").value(hasItem(DEFAULT_BAY_NAME.toString())))
            .andExpect(jsonPath("$.[*].bindingAddress").value(hasItem(DEFAULT_BINDING_ADDRESS.toString())));
    }
    
    @Test
    @Transactional
    public void getBay() throws Exception {
        // Initialize the database
        bayRepository.saveAndFlush(bay);

        // Get the bay
        restBayMockMvc.perform(get("/api/bays/{id}", bay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bay.getId().intValue()))
            .andExpect(jsonPath("$.bayName").value(DEFAULT_BAY_NAME.toString()))
            .andExpect(jsonPath("$.bindingAddress").value(DEFAULT_BINDING_ADDRESS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBay() throws Exception {
        // Get the bay
        restBayMockMvc.perform(get("/api/bays/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBay() throws Exception {
        // Initialize the database
        bayRepository.saveAndFlush(bay);

        int databaseSizeBeforeUpdate = bayRepository.findAll().size();

        // Update the bay
        Bay updatedBay = bayRepository.findById(bay.getId()).get();
        // Disconnect from session so that the updates on updatedBay are not directly saved in db
        em.detach(updatedBay);
        updatedBay
            .bayName(UPDATED_BAY_NAME)
            .bindingAddress(UPDATED_BINDING_ADDRESS);

        restBayMockMvc.perform(put("/api/bays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBay)))
            .andExpect(status().isOk());

        // Validate the Bay in the database
        List<Bay> bayList = bayRepository.findAll();
        assertThat(bayList).hasSize(databaseSizeBeforeUpdate);
        Bay testBay = bayList.get(bayList.size() - 1);
        assertThat(testBay.getBayName()).isEqualTo(UPDATED_BAY_NAME);
        assertThat(testBay.getBindingAddress()).isEqualTo(UPDATED_BINDING_ADDRESS);
    }

    @Test
    @Transactional
    public void updateNonExistingBay() throws Exception {
        int databaseSizeBeforeUpdate = bayRepository.findAll().size();

        // Create the Bay

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBayMockMvc.perform(put("/api/bays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bay)))
            .andExpect(status().isBadRequest());

        // Validate the Bay in the database
        List<Bay> bayList = bayRepository.findAll();
        assertThat(bayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBay() throws Exception {
        // Initialize the database
        bayRepository.saveAndFlush(bay);

        int databaseSizeBeforeDelete = bayRepository.findAll().size();

        // Delete the bay
        restBayMockMvc.perform(delete("/api/bays/{id}", bay.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Bay> bayList = bayRepository.findAll();
        assertThat(bayList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bay.class);
        Bay bay1 = new Bay();
        bay1.setId(1L);
        Bay bay2 = new Bay();
        bay2.setId(bay1.getId());
        assertThat(bay1).isEqualTo(bay2);
        bay2.setId(2L);
        assertThat(bay1).isNotEqualTo(bay2);
        bay1.setId(null);
        assertThat(bay1).isNotEqualTo(bay2);
    }
}
