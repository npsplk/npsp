package lk.npsp.web.rest;
import lk.npsp.domain.Bay;
import lk.npsp.repository.BayRepository;
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
 * REST controller for managing Bay.
 */
@RestController
@RequestMapping("/api")
public class BayResource {

    private final Logger log = LoggerFactory.getLogger(BayResource.class);

    private static final String ENTITY_NAME = "bay";

    private final BayRepository bayRepository;

    public BayResource(BayRepository bayRepository) {
        this.bayRepository = bayRepository;
    }

    /**
     * POST  /bays : Create a new bay.
     *
     * @param bay the bay to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bay, or with status 400 (Bad Request) if the bay has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bays")
    public ResponseEntity<Bay> createBay(@RequestBody Bay bay) throws URISyntaxException {
        log.debug("REST request to save Bay : {}", bay);
        if (bay.getId() != null) {
            throw new BadRequestAlertException("A new bay cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Bay result = bayRepository.save(bay);
        return ResponseEntity.created(new URI("/api/bays/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bays : Updates an existing bay.
     *
     * @param bay the bay to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bay,
     * or with status 400 (Bad Request) if the bay is not valid,
     * or with status 500 (Internal Server Error) if the bay couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bays")
    public ResponseEntity<Bay> updateBay(@RequestBody Bay bay) throws URISyntaxException {
        log.debug("REST request to update Bay : {}", bay);
        if (bay.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Bay result = bayRepository.save(bay);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bay.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bays : get all the bays.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bays in body
     */
    @GetMapping("/bays")
    public ResponseEntity<List<Bay>> getAllBays(Pageable pageable) {
        log.debug("REST request to get a page of Bays");
        Page<Bay> page = bayRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bays");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /bays/:id : get the "id" bay.
     *
     * @param id the id of the bay to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bay, or with status 404 (Not Found)
     */
    @GetMapping("/bays/{id}")
    public ResponseEntity<Bay> getBay(@PathVariable Long id) {
        log.debug("REST request to get Bay : {}", id);
        Optional<Bay> bay = bayRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(bay);
    }

    /**
     * DELETE  /bays/:id : delete the "id" bay.
     *
     * @param id the id of the bay to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bays/{id}")
    public ResponseEntity<Void> deleteBay(@PathVariable Long id) {
        log.debug("REST request to delete Bay : {}", id);
        bayRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
