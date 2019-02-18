package lk.npsp.web.rest;
import lk.npsp.domain.VehicleOwner;
import lk.npsp.repository.VehicleOwnerRepository;
import lk.npsp.repository.search.VehicleOwnerSearchRepository;
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
 * REST controller for managing VehicleOwner.
 */
@RestController
@RequestMapping("/api")
public class VehicleOwnerResource {

    private final Logger log = LoggerFactory.getLogger(VehicleOwnerResource.class);

    private static final String ENTITY_NAME = "vehicleOwner";

    private final VehicleOwnerRepository vehicleOwnerRepository;

    private final VehicleOwnerSearchRepository vehicleOwnerSearchRepository;

    public VehicleOwnerResource(VehicleOwnerRepository vehicleOwnerRepository, VehicleOwnerSearchRepository vehicleOwnerSearchRepository) {
        this.vehicleOwnerRepository = vehicleOwnerRepository;
        this.vehicleOwnerSearchRepository = vehicleOwnerSearchRepository;
    }

    /**
     * POST  /vehicle-owners : Create a new vehicleOwner.
     *
     * @param vehicleOwner the vehicleOwner to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vehicleOwner, or with status 400 (Bad Request) if the vehicleOwner has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/vehicle-owners")
    public ResponseEntity<VehicleOwner> createVehicleOwner(@Valid @RequestBody VehicleOwner vehicleOwner) throws URISyntaxException {
        log.debug("REST request to save VehicleOwner : {}", vehicleOwner);
        if (vehicleOwner.getId() != null) {
            throw new BadRequestAlertException("A new vehicleOwner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VehicleOwner result = vehicleOwnerRepository.save(vehicleOwner);
        vehicleOwnerSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/vehicle-owners/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /vehicle-owners : Updates an existing vehicleOwner.
     *
     * @param vehicleOwner the vehicleOwner to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated vehicleOwner,
     * or with status 400 (Bad Request) if the vehicleOwner is not valid,
     * or with status 500 (Internal Server Error) if the vehicleOwner couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/vehicle-owners")
    public ResponseEntity<VehicleOwner> updateVehicleOwner(@Valid @RequestBody VehicleOwner vehicleOwner) throws URISyntaxException {
        log.debug("REST request to update VehicleOwner : {}", vehicleOwner);
        if (vehicleOwner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        VehicleOwner result = vehicleOwnerRepository.save(vehicleOwner);
        vehicleOwnerSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, vehicleOwner.getId().toString()))
            .body(result);
    }

    /**
     * GET  /vehicle-owners : get all the vehicleOwners.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of vehicleOwners in body
     */
    @GetMapping("/vehicle-owners")
    public ResponseEntity<List<VehicleOwner>> getAllVehicleOwners(Pageable pageable) {
        log.debug("REST request to get a page of VehicleOwners");
        Page<VehicleOwner> page = vehicleOwnerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/vehicle-owners");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /vehicle-owners/:id : get the "id" vehicleOwner.
     *
     * @param id the id of the vehicleOwner to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the vehicleOwner, or with status 404 (Not Found)
     */
    @GetMapping("/vehicle-owners/{id}")
    public ResponseEntity<VehicleOwner> getVehicleOwner(@PathVariable Long id) {
        log.debug("REST request to get VehicleOwner : {}", id);
        Optional<VehicleOwner> vehicleOwner = vehicleOwnerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(vehicleOwner);
    }

    /**
     * DELETE  /vehicle-owners/:id : delete the "id" vehicleOwner.
     *
     * @param id the id of the vehicleOwner to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/vehicle-owners/{id}")
    public ResponseEntity<Void> deleteVehicleOwner(@PathVariable Long id) {
        log.debug("REST request to delete VehicleOwner : {}", id);
        vehicleOwnerRepository.deleteById(id);
        vehicleOwnerSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/vehicle-owners?query=:query : search for the vehicleOwner corresponding
     * to the query.
     *
     * @param query the query of the vehicleOwner search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/vehicle-owners")
    public ResponseEntity<List<VehicleOwner>> searchVehicleOwners(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of VehicleOwners for query {}", query);
        Page<VehicleOwner> page = vehicleOwnerSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/vehicle-owners");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
