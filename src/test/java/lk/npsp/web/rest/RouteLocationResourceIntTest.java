package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.RouteLocation;
import lk.npsp.repository.RouteLocationRepository;
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
 * Test class for the RouteLocationResource REST controller.
 *
 * @see RouteLocationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class RouteLocationResourceIntTest {

    private static final Long DEFAULT_SEQUENCE_NUMBER = 1L;
    private static final Long UPDATED_SEQUENCE_NUMBER = 2L;

    @Autowired
    private RouteLocationRepository routeLocationRepository;

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

    private MockMvc restRouteLocationMockMvc;

    private RouteLocation routeLocation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RouteLocationResource routeLocationResource = new RouteLocationResource(routeLocationRepository);
        this.restRouteLocationMockMvc = MockMvcBuilders.standaloneSetup(routeLocationResource)
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
    public static RouteLocation createEntity(EntityManager em) {
        RouteLocation routeLocation = new RouteLocation()
            .sequenceNumber(DEFAULT_SEQUENCE_NUMBER);
        return routeLocation;
    }

    @Before
    public void initTest() {
        routeLocation = createEntity(em);
    }

    @Test
    @Transactional
    public void createRouteLocation() throws Exception {
        int databaseSizeBeforeCreate = routeLocationRepository.findAll().size();

        // Create the RouteLocation
        restRouteLocationMockMvc.perform(post("/api/route-locations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(routeLocation)))
            .andExpect(status().isCreated());

        // Validate the RouteLocation in the database
        List<RouteLocation> routeLocationList = routeLocationRepository.findAll();
        assertThat(routeLocationList).hasSize(databaseSizeBeforeCreate + 1);
        RouteLocation testRouteLocation = routeLocationList.get(routeLocationList.size() - 1);
        assertThat(testRouteLocation.getSequenceNumber()).isEqualTo(DEFAULT_SEQUENCE_NUMBER);
    }

    @Test
    @Transactional
    public void createRouteLocationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = routeLocationRepository.findAll().size();

        // Create the RouteLocation with an existing ID
        routeLocation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRouteLocationMockMvc.perform(post("/api/route-locations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(routeLocation)))
            .andExpect(status().isBadRequest());

        // Validate the RouteLocation in the database
        List<RouteLocation> routeLocationList = routeLocationRepository.findAll();
        assertThat(routeLocationList).hasSize(databaseSizeBeforeCreate);
    }

//    @Test
//    @Transactional
//    public void getAllRouteLocations() throws Exception {
//        // Initialize the database
//        routeLocationRepository.saveAndFlush(routeLocation);
//
//        // Get all the routeLocationList
//        restRouteLocationMockMvc.perform(get("/api/route-locations?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(routeLocation.getId().intValue())))
//            .andExpect(jsonPath("$.[*].sequenceNumber").value(hasItem(DEFAULT_SEQUENCE_NUMBER.intValue())));
//    }
    
    @Test
    @Transactional
    public void getRouteLocation() throws Exception {
        // Initialize the database
        routeLocationRepository.saveAndFlush(routeLocation);

        // Get the routeLocation
        restRouteLocationMockMvc.perform(get("/api/route-locations/{id}", routeLocation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(routeLocation.getId().intValue()))
            .andExpect(jsonPath("$.sequenceNumber").value(DEFAULT_SEQUENCE_NUMBER.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRouteLocation() throws Exception {
        // Get the routeLocation
        restRouteLocationMockMvc.perform(get("/api/route-locations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRouteLocation() throws Exception {
        // Initialize the database
        routeLocationRepository.saveAndFlush(routeLocation);

        int databaseSizeBeforeUpdate = routeLocationRepository.findAll().size();

        // Update the routeLocation
        RouteLocation updatedRouteLocation = routeLocationRepository.findById(routeLocation.getId()).get();
        // Disconnect from session so that the updates on updatedRouteLocation are not directly saved in db
        em.detach(updatedRouteLocation);
        updatedRouteLocation
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER);

        restRouteLocationMockMvc.perform(put("/api/route-locations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRouteLocation)))
            .andExpect(status().isOk());

        // Validate the RouteLocation in the database
        List<RouteLocation> routeLocationList = routeLocationRepository.findAll();
        assertThat(routeLocationList).hasSize(databaseSizeBeforeUpdate);
        RouteLocation testRouteLocation = routeLocationList.get(routeLocationList.size() - 1);
        assertThat(testRouteLocation.getSequenceNumber()).isEqualTo(UPDATED_SEQUENCE_NUMBER);
    }

    @Test
    @Transactional
    public void updateNonExistingRouteLocation() throws Exception {
        int databaseSizeBeforeUpdate = routeLocationRepository.findAll().size();

        // Create the RouteLocation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteLocationMockMvc.perform(put("/api/route-locations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(routeLocation)))
            .andExpect(status().isBadRequest());

        // Validate the RouteLocation in the database
        List<RouteLocation> routeLocationList = routeLocationRepository.findAll();
        assertThat(routeLocationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRouteLocation() throws Exception {
        // Initialize the database
        routeLocationRepository.saveAndFlush(routeLocation);

        int databaseSizeBeforeDelete = routeLocationRepository.findAll().size();

        // Delete the routeLocation
        restRouteLocationMockMvc.perform(delete("/api/route-locations/{id}", routeLocation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<RouteLocation> routeLocationList = routeLocationRepository.findAll();
        assertThat(routeLocationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RouteLocation.class);
        RouteLocation routeLocation1 = new RouteLocation();
        routeLocation1.setId(1L);
        RouteLocation routeLocation2 = new RouteLocation();
        routeLocation2.setId(routeLocation1.getId());
        assertThat(routeLocation1).isEqualTo(routeLocation2);
        routeLocation2.setId(2L);
        assertThat(routeLocation1).isNotEqualTo(routeLocation2);
        routeLocation1.setId(null);
        assertThat(routeLocation1).isNotEqualTo(routeLocation2);
    }
}
