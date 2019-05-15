package lk.npsp.web.rest;
import lk.npsp.domain.RouteLocation;
import lk.npsp.repository.RouteLocationRepository;
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

/**
 * REST controller for managing RouteLocation.
 */
@RestController
@RequestMapping("/api")
public class RouteLocationResource {

    private final Logger log = LoggerFactory.getLogger(RouteLocationResource.class);

    private static final String ENTITY_NAME = "routeLocation";

    private final RouteLocationRepository routeLocationRepository;

    public RouteLocationResource(RouteLocationRepository routeLocationRepository) {
        this.routeLocationRepository = routeLocationRepository;
    }

    /**
     * POST  /route-locations : Create a new routeLocation.
     *
     * @param routeLocation the routeLocation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new routeLocation, or with status 400 (Bad Request) if the routeLocation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/route-locations")
    public ResponseEntity<RouteLocation> createRouteLocation(@RequestBody RouteLocation routeLocation) throws URISyntaxException {
        log.debug("REST request to save RouteLocation : {}", routeLocation);
        if (routeLocation.getId() != null) {
            throw new BadRequestAlertException("A new routeLocation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RouteLocation result = routeLocationRepository.save(routeLocation);
        return ResponseEntity.created(new URI("/api/route-locations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /route-locations : Updates an existing routeLocation.
     *
     * @param routeLocation the routeLocation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated routeLocation,
     * or with status 400 (Bad Request) if the routeLocation is not valid,
     * or with status 500 (Internal Server Error) if the routeLocation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/route-locations")
    public ResponseEntity<RouteLocation> updateRouteLocation(@RequestBody RouteLocation routeLocation) throws URISyntaxException {
        log.debug("REST request to update RouteLocation : {}", routeLocation);
        if (routeLocation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RouteLocation result = routeLocationRepository.save(routeLocation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, routeLocation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /route-locations : get all the routeLocations by Route.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of routeLocations in body
     */
    @GetMapping("/route-locations")
    public ResponseEntity<List<RouteLocation>> getAllRouteLocations(@RequestParam("route") Long routeId) {
        log.debug("REST request to get a page of RouteLocations");
        List<RouteLocation> result = routeLocationRepository.findRouteLocationsByRoute(routeId);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /all-route-locations : get all the route-locations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of route-locations in body
     */
    @GetMapping("/all-route-locations")
    public ResponseEntity<List<RouteLocation>> getAllRouteLocations() {
        log.debug("REST request to get a list of Route Locations");
        List<RouteLocation> list = routeLocationRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    /**
     * GET  /route-locations/:id : get the "id" routeLocation.
     *
     * @param id the id of the routeLocation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the routeLocation, or with status 404 (Not Found)
     */
    @GetMapping("/route-locations/{id}")
    public ResponseEntity<RouteLocation> getRouteLocation(@PathVariable Long id) {
        log.debug("REST request to get RouteLocation : {}", id);
        Optional<RouteLocation> routeLocation = routeLocationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(routeLocation);
    }

    /**
     * DELETE  /route-locations/:id : delete the "id" routeLocation.
     *
     * @param id the id of the routeLocation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/route-locations/{id}")
    public ResponseEntity<Void> deleteRouteLocation(@PathVariable Long id) {
        log.debug("REST request to delete RouteLocation : {}", id);
        routeLocationRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
