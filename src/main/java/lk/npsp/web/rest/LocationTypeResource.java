package lk.npsp.web.rest;
import lk.npsp.domain.LocationType;
import lk.npsp.repository.LocationTypeRepository;
import lk.npsp.repository.search.LocationTypeSearchRepository;
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
 * REST controller for managing LocationType.
 */
@RestController
@RequestMapping("/api")
public class LocationTypeResource {

    private final Logger log = LoggerFactory.getLogger(LocationTypeResource.class);

    private static final String ENTITY_NAME = "locationType";

    private final LocationTypeRepository locationTypeRepository;

    private final LocationTypeSearchRepository locationTypeSearchRepository;

    public LocationTypeResource(LocationTypeRepository locationTypeRepository, LocationTypeSearchRepository locationTypeSearchRepository) {
        this.locationTypeRepository = locationTypeRepository;
        this.locationTypeSearchRepository = locationTypeSearchRepository;
    }

    /**
     * POST  /location-types : Create a new locationType.
     *
     * @param locationType the locationType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new locationType, or with status 400 (Bad Request) if the locationType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/location-types")
    public ResponseEntity<LocationType> createLocationType(@Valid @RequestBody LocationType locationType) throws URISyntaxException {
        log.debug("REST request to save LocationType : {}", locationType);
        if (locationType.getId() != null) {
            throw new BadRequestAlertException("A new locationType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LocationType result = locationTypeRepository.save(locationType);
        locationTypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/location-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /location-types : Updates an existing locationType.
     *
     * @param locationType the locationType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated locationType,
     * or with status 400 (Bad Request) if the locationType is not valid,
     * or with status 500 (Internal Server Error) if the locationType couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/location-types")
    public ResponseEntity<LocationType> updateLocationType(@Valid @RequestBody LocationType locationType) throws URISyntaxException {
        log.debug("REST request to update LocationType : {}", locationType);
        if (locationType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LocationType result = locationTypeRepository.save(locationType);
        locationTypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, locationType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /location-types : get all the locationTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of locationTypes in body
     */
    @GetMapping("/location-types")
    public ResponseEntity<List<LocationType>> getAllLocationTypes(Pageable pageable) {
        log.debug("REST request to get a page of LocationTypes");
        Page<LocationType> page = locationTypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/location-types");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /location-types/:id : get the "id" locationType.
     *
     * @param id the id of the locationType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the locationType, or with status 404 (Not Found)
     */
    @GetMapping("/location-types/{id}")
    public ResponseEntity<LocationType> getLocationType(@PathVariable Long id) {
        log.debug("REST request to get LocationType : {}", id);
        Optional<LocationType> locationType = locationTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(locationType);
    }

    /**
     * DELETE  /location-types/:id : delete the "id" locationType.
     *
     * @param id the id of the locationType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/location-types/{id}")
    public ResponseEntity<Void> deleteLocationType(@PathVariable Long id) {
        log.debug("REST request to delete LocationType : {}", id);
        locationTypeRepository.deleteById(id);
        locationTypeSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/location-types?query=:query : search for the locationType corresponding
     * to the query.
     *
     * @param query the query of the locationType search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/location-types")
    public ResponseEntity<List<LocationType>> searchLocationTypes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of LocationTypes for query {}", query);
        Page<LocationType> page = locationTypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/location-types");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
