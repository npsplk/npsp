package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.Weekday;
import lk.npsp.repository.WeekdayRepository;
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

import lk.npsp.domain.enumeration.Weekdays;
/**
 * Test class for the WeekdayResource REST controller.
 *
 * @see WeekdayResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class WeekdayResourceIntTest {

    private static final Weekdays DEFAULT_WEEKDAY = Weekdays.Sunday;
    private static final Weekdays UPDATED_WEEKDAY = Weekdays.Monday;

    @Autowired
    private WeekdayRepository weekdayRepository;

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
        final WeekdayResource weekdayResource = new WeekdayResource(weekdayRepository);
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
            .weekday(DEFAULT_WEEKDAY);
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
        assertThat(testWeekday.getWeekday()).isEqualTo(DEFAULT_WEEKDAY);
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
    }

    @Test
    @Transactional
    public void checkWeekdayIsRequired() throws Exception {
        int databaseSizeBeforeTest = weekdayRepository.findAll().size();
        // set the field null
        weekday.setWeekday(null);

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
            .andExpect(jsonPath("$.[*].weekday").value(hasItem(DEFAULT_WEEKDAY.toString())));
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
            .andExpect(jsonPath("$.weekday").value(DEFAULT_WEEKDAY.toString()));
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
            .weekday(UPDATED_WEEKDAY);

        restWeekdayMockMvc.perform(put("/api/weekdays")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWeekday)))
            .andExpect(status().isOk());

        // Validate the Weekday in the database
        List<Weekday> weekdayList = weekdayRepository.findAll();
        assertThat(weekdayList).hasSize(databaseSizeBeforeUpdate);
        Weekday testWeekday = weekdayList.get(weekdayList.size() - 1);
        assertThat(testWeekday.getWeekday()).isEqualTo(UPDATED_WEEKDAY);
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
