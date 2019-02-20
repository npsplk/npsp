package lk.npsp.web.rest;

import com.codahale.metrics.annotation.Timed;
import lk.npsp.domain.ParkingArea;
import lk.npsp.repository.ParkingAreaRepository;
import lk.npsp.repository.search.ParkingAreaSearchRepository;
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

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ParkingArea.
 */
@RestController
@RequestMapping("/api")
public class ParkingAreaResource {

    private final Logger log = LoggerFactory.getLogger(ParkingAreaResource.class);

    private static final String ENTITY_NAME = "parkingArea";

    private final ParkingAreaRepository parkingAreaRepository;

    private final ParkingAreaSearchRepository parkingAreaSearchRepository;

    public ParkingAreaResource(ParkingAreaRepository parkingAreaRepository, ParkingAreaSearchRepository parkingAreaSearchRepository) {
        this.parkingAreaRepository = parkingAreaRepository;
        this.parkingAreaSearchRepository = parkingAreaSearchRepository;
    }

    /**
     * POST  /parking-areas : Create a new parkingArea.
     *
     * @param parkingArea the parkingArea to create
     * @return the ResponseEntity with status 201 (Created) and with body the new parkingArea, or with status 400 (Bad Request) if the parkingArea has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/parking-areas")
    @Timed
    public ResponseEntity<ParkingArea> createParkingArea(@Valid @RequestBody ParkingArea parkingArea) throws URISyntaxException {
        log.debug("REST request to save ParkingArea : {}", parkingArea);
        if (parkingArea.getId() != null) {
            throw new BadRequestAlertException("A new parkingArea cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParkingArea result = parkingAreaRepository.save(parkingArea);
        parkingAreaSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/parking-areas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /parking-areas : Updates an existing parkingArea.
     *
     * @param parkingArea the parkingArea to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated parkingArea,
     * or with status 400 (Bad Request) if the parkingArea is not valid,
     * or with status 500 (Internal Server Error) if the parkingArea couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/parking-areas")
    @Timed
    public ResponseEntity<ParkingArea> updateParkingArea(@Valid @RequestBody ParkingArea parkingArea) throws URISyntaxException {
        log.debug("REST request to update ParkingArea : {}", parkingArea);
        if (parkingArea.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ParkingArea result = parkingAreaRepository.save(parkingArea);
        parkingAreaSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, parkingArea.getId().toString()))
            .body(result);
    }

    /**
     * GET  /parking-areas : get all the parkingAreas.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of parkingAreas in body
     */
    @GetMapping("/parking-areas")
    @Timed
    public ResponseEntity<List<ParkingArea>> getAllParkingAreas(Pageable pageable) {
        log.debug("REST request to get a page of ParkingAreas");
        Page<ParkingArea> page = parkingAreaRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/parking-areas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /parking-areas/:id : get the "id" parkingArea.
     *
     * @param id the id of the parkingArea to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the parkingArea, or with status 404 (Not Found)
     */
    @GetMapping("/parking-areas/{id}")
    @Timed
    public ResponseEntity<ParkingArea> getParkingArea(@PathVariable Long id) {
        log.debug("REST request to get ParkingArea : {}", id);
        Optional<ParkingArea> parkingArea = parkingAreaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(parkingArea);
    }

    /**
     * DELETE  /parking-areas/:id : delete the "id" parkingArea.
     *
     * @param id the id of the parkingArea to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/parking-areas/{id}")
    @Timed
    public ResponseEntity<Void> deleteParkingArea(@PathVariable Long id) {
        log.debug("REST request to delete ParkingArea : {}", id);

        parkingAreaRepository.deleteById(id);
        parkingAreaSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/parking-areas?query=:query : search for the parkingArea corresponding
     * to the query.
     *
     * @param query the query of the parkingArea search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/parking-areas")
    @Timed
    public ResponseEntity<List<ParkingArea>> searchParkingAreas(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ParkingAreas for query {}", query);
        Page<ParkingArea> page = parkingAreaSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/parking-areas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
