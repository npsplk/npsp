package lk.npsp.web.rest;

import lk.npsp.NpspApp;

import lk.npsp.domain.Route;
import lk.npsp.repository.RouteRepository;
import lk.npsp.repository.search.RouteSearchRepository;
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
 * Test class for the RouteResource REST controller.
 *
 * @see RouteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NpspApp.class)
public class RouteResourceIntTest {

    private static final String DEFAULT_ROUTE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ROUTE_NAME = "BBBBBBBBBB";

    @Autowired
    private RouteRepository routeRepository;

    /**
     * This repository is mocked in the lk.npsp.repository.search test package.
     *
     * @see lk.npsp.repository.search.RouteSearchRepositoryMockConfiguration
     */
    @Autowired
    private RouteSearchRepository mockRouteSearchRepository;

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

    private MockMvc restRouteMockMvc;

    private Route route;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RouteResource routeResource = new RouteResource(routeRepository, mockRouteSearchRepository);
        this.restRouteMockMvc = MockMvcBuilders.standaloneSetup(routeResource)
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
    public static Route createEntity(EntityManager em) {
        Route route = new Route()
            .routeName(DEFAULT_ROUTE_NAME);
        return route;
    }

    @Before
    public void initTest() {
        route = createEntity(em);
    }

    @Test
    @Transactional
    public void createRoute() throws Exception {
        int databaseSizeBeforeCreate = routeRepository.findAll().size();

        // Create the Route
        restRouteMockMvc.perform(post("/api/routes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(route)))
            .andExpect(status().isCreated());

        // Validate the Route in the database
        List<Route> routeList = routeRepository.findAll();
        assertThat(routeList).hasSize(databaseSizeBeforeCreate + 1);
        Route testRoute = routeList.get(routeList.size() - 1);
        assertThat(testRoute.getRouteName()).isEqualTo(DEFAULT_ROUTE_NAME);

        // Validate the Route in Elasticsearch
        verify(mockRouteSearchRepository, times(1)).save(testRoute);
    }

    @Test
    @Transactional
    public void createRouteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = routeRepository.findAll().size();

        // Create the Route with an existing ID
        route.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRouteMockMvc.perform(post("/api/routes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(route)))
            .andExpect(status().isBadRequest());

        // Validate the Route in the database
        List<Route> routeList = routeRepository.findAll();
        assertThat(routeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Route in Elasticsearch
        verify(mockRouteSearchRepository, times(0)).save(route);
    }

    @Test
    @Transactional
    public void checkRouteNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = routeRepository.findAll().size();
        // set the field null
        route.setRouteName(null);

        // Create the Route, which fails.

        restRouteMockMvc.perform(post("/api/routes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(route)))
            .andExpect(status().isBadRequest());

        List<Route> routeList = routeRepository.findAll();
        assertThat(routeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRoutes() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

        // Get all the routeList
        restRouteMockMvc.perform(get("/api/routes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(route.getId().intValue())))
            .andExpect(jsonPath("$.[*].routeName").value(hasItem(DEFAULT_ROUTE_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getRoute() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

        // Get the route
        restRouteMockMvc.perform(get("/api/routes/{id}", route.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(route.getId().intValue()))
            .andExpect(jsonPath("$.routeName").value(DEFAULT_ROUTE_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRoute() throws Exception {
        // Get the route
        restRouteMockMvc.perform(get("/api/routes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRoute() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

        int databaseSizeBeforeUpdate = routeRepository.findAll().size();

        // Update the route
        Route updatedRoute = routeRepository.findById(route.getId()).get();
        // Disconnect from session so that the updates on updatedRoute are not directly saved in db
        em.detach(updatedRoute);
        updatedRoute
            .routeName(UPDATED_ROUTE_NAME);

        restRouteMockMvc.perform(put("/api/routes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRoute)))
            .andExpect(status().isOk());

        // Validate the Route in the database
        List<Route> routeList = routeRepository.findAll();
        assertThat(routeList).hasSize(databaseSizeBeforeUpdate);
        Route testRoute = routeList.get(routeList.size() - 1);
        assertThat(testRoute.getRouteName()).isEqualTo(UPDATED_ROUTE_NAME);

        // Validate the Route in Elasticsearch
        verify(mockRouteSearchRepository, times(1)).save(testRoute);
    }

    @Test
    @Transactional
    public void updateNonExistingRoute() throws Exception {
        int databaseSizeBeforeUpdate = routeRepository.findAll().size();

        // Create the Route

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRouteMockMvc.perform(put("/api/routes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(route)))
            .andExpect(status().isBadRequest());

        // Validate the Route in the database
        List<Route> routeList = routeRepository.findAll();
        assertThat(routeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Route in Elasticsearch
        verify(mockRouteSearchRepository, times(0)).save(route);
    }

    @Test
    @Transactional
    public void deleteRoute() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);

        int databaseSizeBeforeDelete = routeRepository.findAll().size();

        // Delete the route
        restRouteMockMvc.perform(delete("/api/routes/{id}", route.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Route> routeList = routeRepository.findAll();
        assertThat(routeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Route in Elasticsearch
        verify(mockRouteSearchRepository, times(1)).deleteById(route.getId());
    }

    @Test
    @Transactional
    public void searchRoute() throws Exception {
        // Initialize the database
        routeRepository.saveAndFlush(route);
        when(mockRouteSearchRepository.search(queryStringQuery("id:" + route.getId())))
            .thenReturn(Collections.singletonList(route));
        // Search the route
        restRouteMockMvc.perform(get("/api/_search/routes?query=id:" + route.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(route.getId().intValue())))
            .andExpect(jsonPath("$.[*].routeName").value(hasItem(DEFAULT_ROUTE_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Route.class);
        Route route1 = new Route();
        route1.setId(1L);
        Route route2 = new Route();
        route2.setId(route1.getId());
        assertThat(route1).isEqualTo(route2);
        route2.setId(2L);
        assertThat(route1).isNotEqualTo(route2);
        route1.setId(null);
        assertThat(route1).isNotEqualTo(route2);
    }
}
