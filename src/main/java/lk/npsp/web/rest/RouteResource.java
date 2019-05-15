package lk.npsp.web.rest;

import lk.npsp.domain.Route;
import lk.npsp.domain.RouteLocation;
import lk.npsp.repository.RouteLocationRepository;
import lk.npsp.repository.RouteRepository;
import lk.npsp.web.rest.errors.BadRequestAlertException;
import lk.npsp.web.rest.util.HeaderUtil;
import lk.npsp.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;

/**
 * REST controller for managing Route.
 */
@RestController
@RequestMapping("/api")
public class RouteResource {

    private final Logger log = LoggerFactory.getLogger(RouteResource.class);

    private static final String ENTITY_NAME = "route";

    private final RouteRepository routeRepository;
    private final RouteLocationRepository routeLocationRepository;

    public RouteResource(RouteRepository routeRepository, RouteLocationRepository routeLocationRepository) {

        this.routeRepository = routeRepository;
        this.routeLocationRepository = routeLocationRepository;
    }

    /**
     * POST  /routes : Create a new route.
     *
     * @param route the route to create
     * @return the ResponseEntity with status 201 (Created) and with body the new route, or with status 400 (Bad Request) if the route has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/routes")
    public ResponseEntity<Route> createRoute(@RequestBody Route route) throws URISyntaxException {
        log.debug("REST request to save Route : {}", route);
        if (route.getId() != null) {
            throw new BadRequestAlertException("A new route cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Route result = routeRepository.save(route);

        for (RouteLocation routeLocation : route.getRouteLocations()) {
            routeLocation.setRoute(result);
            routeLocationRepository.save(routeLocation);
        }

        return ResponseEntity.created(new URI("/api/routes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /routes : Updates an existing route.
     *
     * @param route the route to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated route,
     * or with status 400 (Bad Request) if the route is not valid,
     * or with status 500 (Internal Server Error) if the route couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/routes")
    public ResponseEntity<Route> updateRoute(@RequestBody Route route) throws URISyntaxException {
        log.debug("REST request to update Route : {}", route);
        if (route.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        // remove all route locations of route in database
        List<RouteLocation> routeLocations = routeLocationRepository.
            findRouteLocationsByRoute(route.getId());
        for (RouteLocation routeLocation : routeLocations) {
            routeLocationRepository.delete(routeLocation);
        }

        // save all route locations in request
        for (RouteLocation routeLocation : route.getRouteLocations()) {
            routeLocationRepository.save(routeLocation);
        }

        Route result = routeRepository.save(route);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, route.getId().toString()))
            .body(result);
    }

    /**
     * GET  /routes : get all the routes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of routes in body
     */
    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes(Pageable pageable) {
        log.debug("REST request to get a page of Routes");
        Page<Route> page = routeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/routes");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /all-routes : get all the routes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of routes in body
     */
    @GetMapping("/all-routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        log.debug("REST request to get a list of Routes");
        List<Route> list = routeRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    /**
     * GET  /routes/:id : get the "id" route.
     *
     * @param id the id of the route to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the route, or with status 404 (Not Found)
     */
    @GetMapping("/routes/{id}")
    public ResponseEntity<Route> getRoute(@PathVariable Long id) {
        log.debug("REST request to get Route : {}", id);
        Optional<Route> route = routeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(route);
    }

    /**
     * DELETE  /routes/:id : delete the "id" route.
     *
     * @param id the id of the route to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        log.debug("REST request to delete Route : {}", id);
        routeRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
