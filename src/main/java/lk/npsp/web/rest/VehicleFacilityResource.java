package lk.npsp.web.rest;
import lk.npsp.domain.VehicleFacility;
import lk.npsp.repository.VehicleFacilityRepository;
import lk.npsp.repository.search.VehicleFacilitySearchRepository;
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
 * REST controller for managing VehicleFacility.
 */
@RestController
@RequestMapping("/api")
public class VehicleFacilityResource {

    private final Logger log = LoggerFactory.getLogger(VehicleFacilityResource.class);

    private static final String ENTITY_NAME = "vehicleFacility";

    private final VehicleFacilityRepository vehicleFacilityRepository;

    private final VehicleFacilitySearchRepository vehicleFacilitySearchRepository;

    public VehicleFacilityResource(VehicleFacilityRepository vehicleFacilityRepository, VehicleFacilitySearchRepository vehicleFacilitySearchRepository) {
        this.vehicleFacilityRepository = vehicleFacilityRepository;
        this.vehicleFacilitySearchRepository = vehicleFacilitySearchRepository;
    }

    /**
     * POST  /vehicle-facilities : Create a new vehicleFacility.
     *
     * @param vehicleFacility the vehicleFacility to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vehicleFacility, or with status 400 (Bad Request) if the vehicleFacility has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/vehicle-facilities")
    public ResponseEntity<VehicleFacility> createVehicleFacility(@Valid @RequestBody VehicleFacility vehicleFacility) throws URISyntaxException {
        log.debug("REST request to save VehicleFacility : {}", vehicleFacility);
        if (vehicleFacility.getId() != null) {
            throw new BadRequestAlertException("A new vehicleFacility cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VehicleFacility result = vehicleFacilityRepository.save(vehicleFacility);
        vehicleFacilitySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/vehicle-facilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /vehicle-facilities : Updates an existing vehicleFacility.
     *
     * @param vehicleFacility the vehicleFacility to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated vehicleFacility,
     * or with status 400 (Bad Request) if the vehicleFacility is not valid,
     * or with status 500 (Internal Server Error) if the vehicleFacility couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/vehicle-facilities")
    public ResponseEntity<VehicleFacility> updateVehicleFacility(@Valid @RequestBody VehicleFacility vehicleFacility) throws URISyntaxException {
        log.debug("REST request to update VehicleFacility : {}", vehicleFacility);
        if (vehicleFacility.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VehicleFacility result = vehicleFacilityRepository.save(vehicleFacility);
        vehicleFacilitySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, vehicleFacility.getId().toString()))
            .body(result);
    }

    /**
     * GET  /vehicle-facilities : get all the vehicleFacilities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of vehicleFacilities in body
     */
    @GetMapping("/vehicle-facilities")
    public ResponseEntity<List<VehicleFacility>> getAllVehicleFacilities(Pageable pageable) {
        log.debug("REST request to get a page of VehicleFacilities");
        Page<VehicleFacility> page = vehicleFacilityRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/vehicle-facilities");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /vehicle-facilities/:id : get the "id" vehicleFacility.
     *
     * @param id the id of the vehicleFacility to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the vehicleFacility, or with status 404 (Not Found)
     */
    @GetMapping("/vehicle-facilities/{id}")
    public ResponseEntity<VehicleFacility> getVehicleFacility(@PathVariable Long id) {
        log.debug("REST request to get VehicleFacility : {}", id);
        Optional<VehicleFacility> vehicleFacility = vehicleFacilityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(vehicleFacility);
    }

    /**
     * DELETE  /vehicle-facilities/:id : delete the "id" vehicleFacility.
     *
     * @param id the id of the vehicleFacility to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/vehicle-facilities/{id}")
    public ResponseEntity<Void> deleteVehicleFacility(@PathVariable Long id) {
        log.debug("REST request to delete VehicleFacility : {}", id);
        vehicleFacilityRepository.deleteById(id);
        vehicleFacilitySearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/vehicle-facilities?query=:query : search for the vehicleFacility corresponding
     * to the query.
     *
     * @param query the query of the vehicleFacility search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/vehicle-facilities")
    public ResponseEntity<List<VehicleFacility>> searchVehicleFacilities(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of VehicleFacilities for query {}", query);
        Page<VehicleFacility> page = vehicleFacilitySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/vehicle-facilities");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
