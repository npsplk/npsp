package lk.npsp.web.rest;

import com.codahale.metrics.annotation.Timed;
import lk.npsp.domain.Route;
import lk.npsp.repository.RouteRepository;
import lk.npsp.repository.search.RouteSearchRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Route.
 */
@RestController
@RequestMapping("/api")
public class RouteResource {

    private final Logger log = LoggerFactory.getLogger(RouteResource.class);

    private static final String ENTITY_NAME = "route";

    private final RouteRepository routeRepository;

    private final RouteSearchRepository routeSearchRepository;

    public RouteResource(RouteRepository routeRepository, RouteSearchRepository routeSearchRepository) {
        this.routeRepository = routeRepository;
        this.routeSearchRepository = routeSearchRepository;
    }

    /**
     * POST  /routes : Create a new route.
     *
     * @param route the route to create
     * @return the ResponseEntity with status 201 (Created) and with body the new route, or with status 400 (Bad Request) if the route has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/routes")
    @Timed
    public ResponseEntity<Route> createRoute(@RequestBody Route route) throws URISyntaxException {
        log.debug("REST request to save Route : {}", route);
        if (route.getId() != null) {
            throw new BadRequestAlertException("A new route cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Route result = routeRepository.save(route);
        routeSearchRepository.save(result);
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
    @Timed
    public ResponseEntity<Route> updateRoute(@RequestBody Route route) throws URISyntaxException {
        log.debug("REST request to update Route : {}", route);
        if (route.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Route result = routeRepository.save(route);
        routeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, route.getId().toString()))
            .body(result);
    }

    /**
     * GET  /routes : get all the routes.
     *
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of routes in body
     */
    @GetMapping("/routes")
    @Timed
    public ResponseEntity<List<Route>> getAllRoutes(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Routes");
        Page<Route> page;
        if (eagerload) {
            page = routeRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = routeRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/api/routes?eagerload=%b", eagerload));
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /routes/:id : get the "id" route.
     *
     * @param id the id of the route to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the route, or with status 404 (Not Found)
     */
    @GetMapping("/routes/{id}")
    @Timed
    public ResponseEntity<Route> getRoute(@PathVariable Long id) {
        log.debug("REST request to get Route : {}", id);
        Optional<Route> route = routeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(route);
    }

    /**
     * DELETE  /routes/:id : delete the "id" route.
     *
     * @param id the id of the route to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/routes/{id}")
    @Timed
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        log.debug("REST request to delete Route : {}", id);

        routeRepository.deleteById(id);
        routeSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/routes?query=:query : search for the route corresponding
     * to the query.
     *
     * @param query the query of the route search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/routes")
    @Timed
    public ResponseEntity<List<Route>> searchRoutes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Routes for query {}", query);
        Page<Route> page = routeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/routes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
