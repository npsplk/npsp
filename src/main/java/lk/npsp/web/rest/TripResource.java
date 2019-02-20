package lk.npsp.web.rest;

import com.codahale.metrics.annotation.Timed;
import lk.npsp.domain.Trip;
import lk.npsp.repository.TripRepository;
import lk.npsp.repository.search.TripSearchRepository;
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
 * REST controller for managing Trip.
 */
@RestController
@RequestMapping("/api")
public class TripResource {

    private final Logger log = LoggerFactory.getLogger(TripResource.class);

    private static final String ENTITY_NAME = "trip";

    private final TripRepository tripRepository;

    private final TripSearchRepository tripSearchRepository;

    public TripResource(TripRepository tripRepository, TripSearchRepository tripSearchRepository) {
        this.tripRepository = tripRepository;
        this.tripSearchRepository = tripSearchRepository;
    }

    /**
     * POST  /trips : Create a new trip.
     *
     * @param trip the trip to create
     * @return the ResponseEntity with status 201 (Created) and with body the new trip, or with status 400 (Bad Request) if the trip has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/trips")
    @Timed
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) throws URISyntaxException {
        log.debug("REST request to save Trip : {}", trip);
        if (trip.getId() != null) {
            throw new BadRequestAlertException("A new trip cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Trip result = tripRepository.save(trip);
        tripSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/trips/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trips : Updates an existing trip.
     *
     * @param trip the trip to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated trip,
     * or with status 400 (Bad Request) if the trip is not valid,
     * or with status 500 (Internal Server Error) if the trip couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/trips")
    @Timed
    public ResponseEntity<Trip> updateTrip(@RequestBody Trip trip) throws URISyntaxException {
        log.debug("REST request to update Trip : {}", trip);
        if (trip.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Trip result = tripRepository.save(trip);
        tripSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, trip.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trips : get all the trips.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trips in body
     */
    @GetMapping("/trips")
    @Timed
    public ResponseEntity<List<Trip>> getAllTrips(Pageable pageable) {
        log.debug("REST request to get a page of Trips");
        Page<Trip> page = tripRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trips");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trips/:id : get the "id" trip.
     *
     * @param id the id of the trip to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the trip, or with status 404 (Not Found)
     */
    @GetMapping("/trips/{id}")
    @Timed
    public ResponseEntity<Trip> getTrip(@PathVariable Long id) {
        log.debug("REST request to get Trip : {}", id);
        Optional<Trip> trip = tripRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(trip);
    }

    /**
     * DELETE  /trips/:id : delete the "id" trip.
     *
     * @param id the id of the trip to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/trips/{id}")
    @Timed
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        log.debug("REST request to delete Trip : {}", id);

        tripRepository.deleteById(id);
        tripSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/trips?query=:query : search for the trip corresponding
     * to the query.
     *
     * @param query the query of the trip search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/trips")
    @Timed
    public ResponseEntity<List<Trip>> searchTrips(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Trips for query {}", query);
        Page<Trip> page = tripSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/trips");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
