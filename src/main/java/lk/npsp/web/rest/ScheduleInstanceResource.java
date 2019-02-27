package lk.npsp.web.rest;

import com.codahale.metrics.annotation.Timed;
import lk.npsp.domain.ScheduleInstance;
import lk.npsp.repository.ScheduleInstanceRepository;
import lk.npsp.repository.search.ScheduleInstanceSearchRepository;
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
 * REST controller for managing ScheduleInstance.
 */
@RestController
@RequestMapping("/api")
public class ScheduleInstanceResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleInstanceResource.class);

    private static final String ENTITY_NAME = "scheduleInstance";

    private final ScheduleInstanceRepository scheduleInstanceRepository;

    private final ScheduleInstanceSearchRepository scheduleInstanceSearchRepository;

    public ScheduleInstanceResource(ScheduleInstanceRepository scheduleInstanceRepository, ScheduleInstanceSearchRepository scheduleInstanceSearchRepository) {
        this.scheduleInstanceRepository = scheduleInstanceRepository;
        this.scheduleInstanceSearchRepository = scheduleInstanceSearchRepository;
    }

    /**
     * POST  /schedule-instances : Create a new scheduleInstance.
     *
     * @param scheduleInstance the scheduleInstance to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scheduleInstance, or with status 400 (Bad Request) if the scheduleInstance has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/schedule-instances")
    @Timed
    public ResponseEntity<ScheduleInstance> createScheduleInstance(@RequestBody ScheduleInstance scheduleInstance) throws URISyntaxException {
        log.debug("REST request to save ScheduleInstance : {}", scheduleInstance);
        if (scheduleInstance.getId() != null) {
            throw new BadRequestAlertException("A new scheduleInstance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ScheduleInstance result = scheduleInstanceRepository.save(scheduleInstance);
        scheduleInstanceSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/schedule-instances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /schedule-instances : Updates an existing scheduleInstance.
     *
     * @param scheduleInstance the scheduleInstance to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scheduleInstance,
     * or with status 400 (Bad Request) if the scheduleInstance is not valid,
     * or with status 500 (Internal Server Error) if the scheduleInstance couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/schedule-instances")
    @Timed
    public ResponseEntity<ScheduleInstance> updateScheduleInstance(@RequestBody ScheduleInstance scheduleInstance) throws URISyntaxException {
        log.debug("REST request to update ScheduleInstance : {}", scheduleInstance);
        if (scheduleInstance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ScheduleInstance result = scheduleInstanceRepository.save(scheduleInstance);
        scheduleInstanceSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scheduleInstance.getId().toString()))
            .body(result);
    }

    /**
     * GET  /schedule-instances : get all the scheduleInstances.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scheduleInstances in body
     */
    @GetMapping("/schedule-instances")
    @Timed
    public ResponseEntity<List<ScheduleInstance>> getAllScheduleInstances(Pageable pageable) {
        log.debug("REST request to get a page of ScheduleInstances");
        Page<ScheduleInstance> page = scheduleInstanceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/schedule-instances");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /schedule-instances/:id : get the "id" scheduleInstance.
     *
     * @param id the id of the scheduleInstance to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scheduleInstance, or with status 404 (Not Found)
     */
    @GetMapping("/schedule-instances/{id}")
    @Timed
    public ResponseEntity<ScheduleInstance> getScheduleInstance(@PathVariable Long id) {
        log.debug("REST request to get ScheduleInstance : {}", id);
        Optional<ScheduleInstance> scheduleInstance = scheduleInstanceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(scheduleInstance);
    }

    /**
     * DELETE  /schedule-instances/:id : delete the "id" scheduleInstance.
     *
     * @param id the id of the scheduleInstance to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/schedule-instances/{id}")
    @Timed
    public ResponseEntity<Void> deleteScheduleInstance(@PathVariable Long id) {
        log.debug("REST request to delete ScheduleInstance : {}", id);

        scheduleInstanceRepository.deleteById(id);
        scheduleInstanceSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/schedule-instances?query=:query : search for the scheduleInstance corresponding
     * to the query.
     *
     * @param query the query of the scheduleInstance search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/schedule-instances")
    @Timed
    public ResponseEntity<List<ScheduleInstance>> searchScheduleInstances(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ScheduleInstances for query {}", query);
        Page<ScheduleInstance> page = scheduleInstanceSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/schedule-instances");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
