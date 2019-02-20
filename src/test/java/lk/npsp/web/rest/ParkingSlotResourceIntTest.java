package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.ParkingSlot;
import lk.npsp.repository.ParkingSlotRepository;
import lk.npsp.repository.search.ParkingSlotSearchRepository;
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
 * Test class for the ParkingSlotResource REST controller.
 *
 * @see ParkingSlotResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class ParkingSlotResourceIntTest {

    private static final String DEFAULT_SLOT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SLOT_NUMBER = "BBBBBBBBBB";

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;


    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.ParkingSlotSearchRepositoryMockConfiguration
     */
    @Autowired
    private ParkingSlotSearchRepository mockParkingSlotSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restParkingSlotMockMvc;

    private ParkingSlot parkingSlot;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ParkingSlotResource parkingSlotResource = new ParkingSlotResource(parkingSlotRepository, mockParkingSlotSearchRepository);
        this.restParkingSlotMockMvc = MockMvcBuilders.standaloneSetup(parkingSlotResource)
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
    public static ParkingSlot createEntity(EntityManager em) {
        ParkingSlot parkingSlot = new ParkingSlot()
            .slotNumber(DEFAULT_SLOT_NUMBER);
        return parkingSlot;
    }

    @Before
    public void initTest() {
        parkingSlot = createEntity(em);
    }

    @Test
    @Transactional
    public void createParkingSlot() throws Exception {
        int databaseSizeBeforeCreate = parkingSlotRepository.findAll().size();

        // Create the ParkingSlot
        restParkingSlotMockMvc.perform(post("/api/parking-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(parkingSlot)))
            .andExpect(status().isCreated());

        // Validate the ParkingSlot in the database
        List<ParkingSlot> parkingSlotList = parkingSlotRepository.findAll();
        assertThat(parkingSlotList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingSlot testParkingSlot = parkingSlotList.get(parkingSlotList.size() - 1);
        assertThat(testParkingSlot.getSlotNumber()).isEqualTo(DEFAULT_SLOT_NUMBER);

        // Validate the ParkingSlot in Elasticsearch
        verify(mockParkingSlotSearchRepository, times(1)).save(testParkingSlot);
    }

    @Test
    @Transactional
    public void createParkingSlotWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = parkingSlotRepository.findAll().size();

        // Create the ParkingSlot with an existing ID
        parkingSlot.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restParkingSlotMockMvc.perform(post("/api/parking-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(parkingSlot)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingSlot in the database
        List<ParkingSlot> parkingSlotList = parkingSlotRepository.findAll();
        assertThat(parkingSlotList).hasSize(databaseSizeBeforeCreate);

        // Validate the ParkingSlot in Elasticsearch
        verify(mockParkingSlotSearchRepository, times(0)).save(parkingSlot);
    }

    @Test
    @Transactional
    public void getAllParkingSlots() throws Exception {
        // Initialize the database
        parkingSlotRepository.saveAndFlush(parkingSlot);

        // Get all the parkingSlotList
        restParkingSlotMockMvc.perform(get("/api/parking-slots?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingSlot.getId().intValue())))
            .andExpect(jsonPath("$.[*].slotNumber").value(hasItem(DEFAULT_SLOT_NUMBER.toString())));
    }
    

    @Test
    @Transactional
    public void getParkingSlot() throws Exception {
        // Initialize the database
        parkingSlotRepository.saveAndFlush(parkingSlot);

        // Get the parkingSlot
        restParkingSlotMockMvc.perform(get("/api/parking-slots/{id}", parkingSlot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(parkingSlot.getId().intValue()))
            .andExpect(jsonPath("$.slotNumber").value(DEFAULT_SLOT_NUMBER.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingParkingSlot() throws Exception {
        // Get the parkingSlot
        restParkingSlotMockMvc.perform(get("/api/parking-slots/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParkingSlot() throws Exception {
        // Initialize the database
        parkingSlotRepository.saveAndFlush(parkingSlot);

        int databaseSizeBeforeUpdate = parkingSlotRepository.findAll().size();

        // Update the parkingSlot
        ParkingSlot updatedParkingSlot = parkingSlotRepository.findById(parkingSlot.getId()).get();
        // Disconnect from session so that the updates on updatedParkingSlot are not directly saved in db
        em.detach(updatedParkingSlot);
        updatedParkingSlot
            .slotNumber(UPDATED_SLOT_NUMBER);

        restParkingSlotMockMvc.perform(put("/api/parking-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedParkingSlot)))
            .andExpect(status().isOk());

        // Validate the ParkingSlot in the database
        List<ParkingSlot> parkingSlotList = parkingSlotRepository.findAll();
        assertThat(parkingSlotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSlot testParkingSlot = parkingSlotList.get(parkingSlotList.size() - 1);
        assertThat(testParkingSlot.getSlotNumber()).isEqualTo(UPDATED_SLOT_NUMBER);

        // Validate the ParkingSlot in Elasticsearch
        verify(mockParkingSlotSearchRepository, times(1)).save(testParkingSlot);
    }

    @Test
    @Transactional
    public void updateNonExistingParkingSlot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSlotRepository.findAll().size();

        // Create the ParkingSlot

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restParkingSlotMockMvc.perform(put("/api/parking-slots")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(parkingSlot)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingSlot in the database
        List<ParkingSlot> parkingSlotList = parkingSlotRepository.findAll();
        assertThat(parkingSlotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingSlot in Elasticsearch
        verify(mockParkingSlotSearchRepository, times(0)).save(parkingSlot);
    }

    @Test
    @Transactional
    public void deleteParkingSlot() throws Exception {
        // Initialize the database
        parkingSlotRepository.saveAndFlush(parkingSlot);

        int databaseSizeBeforeDelete = parkingSlotRepository.findAll().size();

        // Get the parkingSlot
        restParkingSlotMockMvc.perform(delete("/api/parking-slots/{id}", parkingSlot.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ParkingSlot> parkingSlotList = parkingSlotRepository.findAll();
        assertThat(parkingSlotList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ParkingSlot in Elasticsearch
        verify(mockParkingSlotSearchRepository, times(1)).deleteById(parkingSlot.getId());
    }

    @Test
    @Transactional
    public void searchParkingSlot() throws Exception {
        // Initialize the database
        parkingSlotRepository.saveAndFlush(parkingSlot);
        when(mockParkingSlotSearchRepository.search(queryStringQuery("id:" + parkingSlot.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(parkingSlot), PageRequest.of(0, 1), 1));
        // Search the parkingSlot
        restParkingSlotMockMvc.perform(get("/api/_search/parking-slots?query=id:" + parkingSlot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingSlot.getId().intValue())))
            .andExpect(jsonPath("$.[*].slotNumber").value(hasItem(DEFAULT_SLOT_NUMBER.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParkingSlot.class);
        ParkingSlot parkingSlot1 = new ParkingSlot();
        parkingSlot1.setId(1L);
        ParkingSlot parkingSlot2 = new ParkingSlot();
        parkingSlot2.setId(parkingSlot1.getId());
        assertThat(parkingSlot1).isEqualTo(parkingSlot2);
        parkingSlot2.setId(2L);
        assertThat(parkingSlot1).isNotEqualTo(parkingSlot2);
        parkingSlot1.setId(null);
        assertThat(parkingSlot1).isNotEqualTo(parkingSlot2);
    }
}
