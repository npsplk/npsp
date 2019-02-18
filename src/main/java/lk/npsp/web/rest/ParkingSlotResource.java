package lk.npsp.web.rest;
import lk.npsp.domain.ParkingSlot;
import lk.npsp.repository.ParkingSlotRepository;
import lk.npsp.repository.search.ParkingSlotSearchRepository;
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
 * REST controller for managing ParkingSlot.
 */
@RestController
@RequestMapping("/api")
public class ParkingSlotResource {

    private final Logger log = LoggerFactory.getLogger(ParkingSlotResource.class);

    private static final String ENTITY_NAME = "parkingSlot";

    private final ParkingSlotRepository parkingSlotRepository;

    private final ParkingSlotSearchRepository parkingSlotSearchRepository;

    public ParkingSlotResource(ParkingSlotRepository parkingSlotRepository, ParkingSlotSearchRepository parkingSlotSearchRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSlotSearchRepository = parkingSlotSearchRepository;
    }

    /**
     * POST  /parking-slots : Create a new parkingSlot.
     *
     * @param parkingSlot the parkingSlot to create
     * @return the ResponseEntity with status 201 (Created) and with body the new parkingSlot, or with status 400 (Bad Request) if the parkingSlot has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/parking-slots")
    public ResponseEntity<ParkingSlot> createParkingSlot(@RequestBody ParkingSlot parkingSlot) throws URISyntaxException {
        log.debug("REST request to save ParkingSlot : {}", parkingSlot);
        if (parkingSlot.getId() != null) {
            throw new BadRequestAlertException("A new parkingSlot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParkingSlot result = parkingSlotRepository.save(parkingSlot);
        parkingSlotSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/parking-slots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /parking-slots : Updates an existing parkingSlot.
     *
     * @param parkingSlot the parkingSlot to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated parkingSlot,
     * or with status 400 (Bad Request) if the parkingSlot is not valid,
     * or with status 500 (Internal Server Error) if the parkingSlot couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/parking-slots")
    public ResponseEntity<ParkingSlot> updateParkingSlot(@RequestBody ParkingSlot parkingSlot) throws URISyntaxException {
        log.debug("REST request to update ParkingSlot : {}", parkingSlot);
        if (parkingSlot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ParkingSlot result = parkingSlotRepository.save(parkingSlot);
        parkingSlotSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, parkingSlot.getId().toString()))
            .body(result);
    }

    /**
     * GET  /parking-slots : get all the parkingSlots.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of parkingSlots in body
     */
    @GetMapping("/parking-slots")
    public ResponseEntity<List<ParkingSlot>> getAllParkingSlots(Pageable pageable) {
        log.debug("REST request to get a page of ParkingSlots");
        Page<ParkingSlot> page = parkingSlotRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/parking-slots");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /parking-slots/:id : get the "id" parkingSlot.
     *
     * @param id the id of the parkingSlot to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the parkingSlot, or with status 404 (Not Found)
     */
    @GetMapping("/parking-slots/{id}")
    public ResponseEntity<ParkingSlot> getParkingSlot(@PathVariable Long id) {
        log.debug("REST request to get ParkingSlot : {}", id);
        Optional<ParkingSlot> parkingSlot = parkingSlotRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(parkingSlot);
    }

    /**
     * DELETE  /parking-slots/:id : delete the "id" parkingSlot.
     *
     * @param id the id of the parkingSlot to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/parking-slots/{id}")
    public ResponseEntity<Void> deleteParkingSlot(@PathVariable Long id) {
        log.debug("REST request to delete ParkingSlot : {}", id);
        parkingSlotRepository.deleteById(id);
        parkingSlotSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/parking-slots?query=:query : search for the parkingSlot corresponding
     * to the query.
     *
     * @param query the query of the parkingSlot search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/parking-slots")
    public ResponseEntity<List<ParkingSlot>> searchParkingSlots(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ParkingSlots for query {}", query);
        Page<ParkingSlot> page = parkingSlotSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/parking-slots");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
