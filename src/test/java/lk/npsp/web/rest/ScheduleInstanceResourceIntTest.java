package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.ScheduleInstance;
import lk.npsp.repository.ScheduleInstanceRepository;
import lk.npsp.repository.ScheduleTemplateRepository;
import lk.npsp.service.ScheduleInstanceManager;
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
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static lk.npsp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import lk.npsp.domain.enumeration.ScheduleState;

/**
 * Test class for the ScheduleInstanceResource REST controller.
 *
 * @see ScheduleInstanceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class ScheduleInstanceResourceIntTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_SCHEDULED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SCHEDULED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTUAL_SCHEDULED_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_SCHEDULED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTUAL_DEPARTURE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_DEPARTURE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SPECIAL_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_SPECIAL_NOTES = "BBBBBBBBBB";

    private static final ScheduleState DEFAULT_SCHEDULE_STATE = ScheduleState.DEPARTED;
    private static final ScheduleState UPDATED_SCHEDULE_STATE = ScheduleState.BOARDING;

    @Autowired
    private ScheduleInstanceRepository scheduleInstanceRepository;

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @Autowired
    private ScheduleInstanceManager scheduleInstanceManager;

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

    private MockMvc restScheduleInstanceMockMvc;

    private ScheduleInstance scheduleInstance;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ScheduleInstanceResource scheduleInstanceResource = new ScheduleInstanceResource(
            scheduleInstanceRepository, scheduleTemplateRepository, scheduleInstanceManager);
        this.restScheduleInstanceMockMvc = MockMvcBuilders.standaloneSetup(scheduleInstanceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleInstance createEntity(EntityManager em) {
        ScheduleInstance scheduleInstance = new ScheduleInstance()
            .date(DEFAULT_DATE)
            .scheduledTime(DEFAULT_SCHEDULED_TIME)
            .actualScheduledTime(DEFAULT_ACTUAL_SCHEDULED_TIME)
            .actualDepartureTime(DEFAULT_ACTUAL_DEPARTURE_TIME)
            .specialNotes(DEFAULT_SPECIAL_NOTES)
            .scheduleState(DEFAULT_SCHEDULE_STATE);
        return scheduleInstance;
    }

    @Before
    public void initTest() {
        scheduleInstance = createEntity(em);
    }

    @Test
    @Transactional
    public void createScheduleInstance() throws Exception {
        int databaseSizeBeforeCreate = scheduleInstanceRepository.findAll().size();

        // Create the ScheduleInstance
        restScheduleInstanceMockMvc.perform(post("/api/schedule-instances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleInstance)))
            .andExpect(status().isCreated());

        // Validate the ScheduleInstance in the database
        List<ScheduleInstance> scheduleInstanceList = scheduleInstanceRepository.findAll();
        assertThat(scheduleInstanceList).hasSize(databaseSizeBeforeCreate + 1);
        ScheduleInstance testScheduleInstance = scheduleInstanceList.get(scheduleInstanceList.size() - 1);
        assertThat(testScheduleInstance.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testScheduleInstance.getScheduledTime()).isEqualTo(DEFAULT_SCHEDULED_TIME);
        assertThat(testScheduleInstance.getActualScheduledTime()).isEqualTo(DEFAULT_ACTUAL_SCHEDULED_TIME);
        assertThat(testScheduleInstance.getActualDepartureTime()).isEqualTo(DEFAULT_ACTUAL_DEPARTURE_TIME);
        assertThat(testScheduleInstance.getSpecialNotes()).isEqualTo(DEFAULT_SPECIAL_NOTES);
        assertThat(testScheduleInstance.getScheduleState()).isEqualTo(DEFAULT_SCHEDULE_STATE);
    }

    @Test
    @Transactional
    public void createScheduleInstanceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scheduleInstanceRepository.findAll().size();

        // Create the ScheduleInstance with an existing ID
        scheduleInstance.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleInstanceMockMvc.perform(post("/api/schedule-instances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleInstance)))
            .andExpect(status().isBadRequest());

        // Validate the ScheduleInstance in the database
        List<ScheduleInstance> scheduleInstanceList = scheduleInstanceRepository.findAll();
        assertThat(scheduleInstanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllScheduleInstances() throws Exception {
        // Initialize the database
        scheduleInstanceRepository.saveAndFlush(scheduleInstance);

        // Get all the scheduleInstanceList
        restScheduleInstanceMockMvc.perform(get("/api/schedule-instances?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduleInstance.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].scheduledTime").value(hasItem(DEFAULT_SCHEDULED_TIME.toString())))
            .andExpect(jsonPath("$.[*].actualScheduledTime").value(hasItem(DEFAULT_ACTUAL_SCHEDULED_TIME.toString())))
            .andExpect(jsonPath("$.[*].actualDepartureTime").value(hasItem(DEFAULT_ACTUAL_DEPARTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].specialNotes").value(hasItem(DEFAULT_SPECIAL_NOTES.toString())))
            .andExpect(jsonPath("$.[*].scheduleState").value(hasItem(DEFAULT_SCHEDULE_STATE.toString())));
    }

    @Test
    @Transactional
    public void getScheduleInstance() throws Exception {
        // Initialize the database
        scheduleInstanceRepository.saveAndFlush(scheduleInstance);

        // Get the scheduleInstance
        restScheduleInstanceMockMvc.perform(get("/api/schedule-instances/{id}", scheduleInstance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scheduleInstance.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.scheduledTime").value(DEFAULT_SCHEDULED_TIME.toString()))
            .andExpect(jsonPath("$.actualScheduledTime").value(DEFAULT_ACTUAL_SCHEDULED_TIME.toString()))
            .andExpect(jsonPath("$.actualDepartureTime").value(DEFAULT_ACTUAL_DEPARTURE_TIME.toString()))
            .andExpect(jsonPath("$.specialNotes").value(DEFAULT_SPECIAL_NOTES.toString()))
            .andExpect(jsonPath("$.scheduleState").value(DEFAULT_SCHEDULE_STATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScheduleInstance() throws Exception {
        // Get the scheduleInstance
        restScheduleInstanceMockMvc.perform(get("/api/schedule-instances/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScheduleInstance() throws Exception {
        // Initialize the database
        scheduleInstanceRepository.saveAndFlush(scheduleInstance);

        int databaseSizeBeforeUpdate = scheduleInstanceRepository.findAll().size();

        // Update the scheduleInstance
        ScheduleInstance updatedScheduleInstance = scheduleInstanceRepository.findById(scheduleInstance.getId()).get();
        // Disconnect from session so that the updates on updatedScheduleInstance are not directly saved in db
        em.detach(updatedScheduleInstance);
        updatedScheduleInstance
            .date(UPDATED_DATE)
            .scheduledTime(UPDATED_SCHEDULED_TIME)
            .actualScheduledTime(UPDATED_ACTUAL_SCHEDULED_TIME)
            .actualDepartureTime(UPDATED_ACTUAL_DEPARTURE_TIME)
            .specialNotes(UPDATED_SPECIAL_NOTES)
            .scheduleState(UPDATED_SCHEDULE_STATE);

        restScheduleInstanceMockMvc.perform(put("/api/schedule-instances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScheduleInstance)))
            .andExpect(status().isOk());

        // Validate the ScheduleInstance in the database
        List<ScheduleInstance> scheduleInstanceList = scheduleInstanceRepository.findAll();
        assertThat(scheduleInstanceList).hasSize(databaseSizeBeforeUpdate);
        ScheduleInstance testScheduleInstance = scheduleInstanceList.get(scheduleInstanceList.size() - 1);
        assertThat(testScheduleInstance.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testScheduleInstance.getScheduledTime()).isEqualTo(UPDATED_SCHEDULED_TIME);
        assertThat(testScheduleInstance.getActualScheduledTime()).isEqualTo(UPDATED_ACTUAL_SCHEDULED_TIME);
        assertThat(testScheduleInstance.getActualDepartureTime()).isEqualTo(UPDATED_ACTUAL_DEPARTURE_TIME);
        assertThat(testScheduleInstance.getSpecialNotes()).isEqualTo(UPDATED_SPECIAL_NOTES);
        assertThat(testScheduleInstance.getScheduleState()).isEqualTo(UPDATED_SCHEDULE_STATE);
    }

    @Test
    @Transactional
    public void updateNonExistingScheduleInstance() throws Exception {
        int databaseSizeBeforeUpdate = scheduleInstanceRepository.findAll().size();

        // Create the ScheduleInstance

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleInstanceMockMvc.perform(put("/api/schedule-instances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduleInstance)))
            .andExpect(status().isBadRequest());

        // Validate the ScheduleInstance in the database
        List<ScheduleInstance> scheduleInstanceList = scheduleInstanceRepository.findAll();
        assertThat(scheduleInstanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteScheduleInstance() throws Exception {
        // Initialize the database
        scheduleInstanceRepository.saveAndFlush(scheduleInstance);

        int databaseSizeBeforeDelete = scheduleInstanceRepository.findAll().size();

        // Delete the scheduleInstance
        restScheduleInstanceMockMvc.perform(delete("/api/schedule-instances/{id}", scheduleInstance.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ScheduleInstance> scheduleInstanceList = scheduleInstanceRepository.findAll();
        assertThat(scheduleInstanceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleInstance.class);
        ScheduleInstance scheduleInstance1 = new ScheduleInstance();
        scheduleInstance1.setId(1L);
        ScheduleInstance scheduleInstance2 = new ScheduleInstance();
        scheduleInstance2.setId(scheduleInstance1.getId());
        assertThat(scheduleInstance1).isEqualTo(scheduleInstance2);
        scheduleInstance2.setId(2L);
        assertThat(scheduleInstance1).isNotEqualTo(scheduleInstance2);
        scheduleInstance1.setId(null);
        assertThat(scheduleInstance1).isNotEqualTo(scheduleInstance2);
    }
}
