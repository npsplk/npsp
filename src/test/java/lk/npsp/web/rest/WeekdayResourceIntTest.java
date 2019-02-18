package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.Weekday;
import lk.npsp.repository.WeekdayRepository;
import lk.npsp.repository.search.WeekdaySearchRepository;
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
 * Test class for the WeekdayResource REST controller.
 *
 * @see WeekdayResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class WeekdayResourceIntTest {

    private static final String DEFAULT_WEEKDAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_WEEKDAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WEEKDAY_META = "AAAAAAAAAA";
    private static final String UPDATED_WEEKDAY_META = "BBBBBBBBBB";

    @Autowired
    private WeekdayRepository weekdayRepository;

    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.WeekdaySearchRepositoryMockConfiguration
     */
    @Autowired
    private WeekdaySearchRepository mockWeekdaySearchRepository;

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

    private MockMvc restWeekdayMockMvc;

    private Weekday weekday;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WeekdayResource weekdayResource = new WeekdayResource(weekdayRepository, mockWeekdaySearchRepository);
        this.restWeekdayMockMvc = MockMvcBuilders.standaloneSetup(weekdayResource)
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
    public static Weekday createEntity(EntityManager em) {
        Weekday weekday = new Weekday()
            .weekdayName(DEFAULT_WEEKDAY_NAME)
            .weekdayMeta(DEFAULT_WEEKDAY_META);
        return weekday;
    }

    @Before
    public void initTest() {
        weekday = createEntity(em);
    }

    @Test
    @Transactional
    public void createWeekday() throws Exception {
        int databaseSizeBeforeCreate = weekdayRepository.findAll().size();

        // Create the Weekday
        restWeekdayMockMvc.perform(post("/api/weekdays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(weekday)))
            .andExpect(status().isCreated());

        // Validate the Weekday in the database
        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeCreate + 1);
        Weekday testWeekday = weekdayList.get(weekdayList.size() - 1);
        assertThat(testWeekday.getWeekdayName()).isEqualTo(DEFAULT_WEEKDAY_NAME);
        assertThat(testWeekday.getWeekdayMeta()).isEqualTo(DEFAULT_WEEKDAY_META);

        // Validate the Weekday in Elasticsearch
        verify(mockWeekdaySearchRepository, times(1)).save(testWeekday);
    }

    @Test
    @Transactional
    public void createWeekdayWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = weekdayRepository.findAll().size();

        // Create the Weekday with an existing ID
        weekday.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWeekdayMockMvc.perform(post("/api/weekdays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(weekday)))
            .andExpect(status().isBadRequest());

        // Validate the Weekday in the database
        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeCreate);

        // Validate the Weekday in Elasticsearch
        verify(mockWeekdaySearchRepository, times(0)).save(weekday);
    }

    @Test
    @Transactional
    public void checkWeekdayNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = weekdayRepository.findAll().size();
        // set the field null
        weekday.setWeekdayName(null);

        // Create the Weekday, which fails.

        restWeekdayMockMvc.perform(post("/api/weekdays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(weekday)))
            .andExpect(status().isBadRequest());

        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWeekdayMetaIsRequired() throws Exception {
        int databaseSizeBeforeTest = weekdayRepository.findAll().size();
        // set the field null
        weekday.setWeekdayMeta(null);

        // Create the Weekday, which fails.

        restWeekdayMockMvc.perform(post("/api/weekdays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(weekday)))
            .andExpect(status().isBadRequest());

        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWeekdays() throws Exception {
        // Initialize the database
        weekdayRepository.saveAndFlush(weekday);

        // Get all the weekdayList
        restWeekdayMockMvc.perform(get("/api/weekdays?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weekday.getId().intValue())))
            .andExpect(jsonPath("$.[*].weekdayName").value(hasItem(DEFAULT_WEEKDAY_NAME.toString())))
            .andExpect(jsonPath("$.[*].weekdayMeta").value(hasItem(DEFAULT_WEEKDAY_META.toString())));
    }
    
    @Test
    @Transactional
    public void getWeekday() throws Exception {
        // Initialize the database
        weekdayRepository.saveAndFlush(weekday);

        // Get the weekday
        restWeekdayMockMvc.perform(get("/api/weekdays/{id}", weekday.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(weekday.getId().intValue()))
            .andExpect(jsonPath("$.weekdayName").value(DEFAULT_WEEKDAY_NAME.toString()))
            .andExpect(jsonPath("$.weekdayMeta").value(DEFAULT_WEEKDAY_META.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWeekday() throws Exception {
        // Get the weekday
        restWeekdayMockMvc.perform(get("/api/weekdays/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWeekday() throws Exception {
        // Initialize the database
        weekdayRepository.saveAndFlush(weekday);

        int databaseSizeBeforeUpdate = weekdayRepository.findAll().size();

        // Update the weekday
        Weekday updatedWeekday = weekdayRepository.findById(weekday.getId()).get();
        // Disconnect from session so that the updates on updatedWeekday are not directly saved in db
        em.detach(updatedWeekday);
        updatedWeekday
            .weekdayName(UPDATED_WEEKDAY_NAME)
            .weekdayMeta(UPDATED_WEEKDAY_META);

        restWeekdayMockMvc.perform(put("/api/weekdays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWeekday)))
            .andExpect(status().isOk());

        // Validate the Weekday in the database
        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeUpdate);
        Weekday testWeekday = weekdayList.get(weekdayList.size() - 1);
        assertThat(testWeekday.getWeekdayName()).isEqualTo(UPDATED_WEEKDAY_NAME);
        assertThat(testWeekday.getWeekdayMeta()).isEqualTo(UPDATED_WEEKDAY_META);

        // Validate the Weekday in Elasticsearch
        verify(mockWeekdaySearchRepository, times(1)).save(testWeekday);
    }

    @Test
    @Transactional
    public void updateNonExistingWeekday() throws Exception {
        int databaseSizeBeforeUpdate = weekdayRepository.findAll().size();

        // Create the Weekday

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWeekdayMockMvc.perform(put("/api/weekdays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(weekday)))
            .andExpect(status().isBadRequest());

        // Validate the Weekday in the database
        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Weekday in Elasticsearch
        verify(mockWeekdaySearchRepository, times(0)).save(weekday);
    }

    @Test
    @Transactional
    public void deleteWeekday() throws Exception {
        // Initialize the database
        weekdayRepository.saveAndFlush(weekday);

        int databaseSizeBeforeDelete = weekdayRepository.findAll().size();

        // Delete the weekday
        restWeekdayMockMvc.perform(delete("/api/weekdays/{id}", weekday.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Weekday in Elasticsearch
        verify(mockWeekdaySearchRepository, times(1)).deleteById(weekday.getId());
    }

    @Test
    @Transactional
    public void searchWeekday() throws Exception {
        // Initialize the database
        weekdayRepository.saveAndFlush(weekday);
        when(mockWeekdaySearchRepository.search(queryStringQuery("id:" + weekday.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(weekday), PageRequest.of(0, 1), 1));
        // Search the weekday
        restWeekdayMockMvc.perform(get("/api/_search/weekdays?query=id:" + weekday.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weekday.getId().intValue())))
            .andExpect(jsonPath("$.[*].weekdayName").value(hasItem(DEFAULT_WEEKDAY_NAME)))
            .andExpect(jsonPath("$.[*].weekdayMeta").value(hasItem(DEFAULT_WEEKDAY_META)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Weekday.class);
        Weekday weekday1 = new Weekday();
        weekday1.setId(1L);
        Weekday weekday2 = new Weekday();
        weekday2.setId(weekday1.getId());
        assertThat(weekday1).isEqualTo(weekday2);
        weekday2.setId(2L);
        assertThat(weekday1).isNotEqualTo(weekday2);
        weekday1.setId(null);
        assertThat(weekday1).isNotEqualTo(weekday2);
    }
}
