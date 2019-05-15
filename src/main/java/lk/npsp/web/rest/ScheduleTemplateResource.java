package lk.npsp.web.rest;
import lk.npsp.domain.ScheduleTemplate;
import lk.npsp.repository.ScheduleTemplateRepository;
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

/**
 * REST controller for managing ScheduleTemplate.
 */
@RestController
@RequestMapping("/api")
public class ScheduleTemplateResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleTemplateResource.class);

    private static final String ENTITY_NAME = "scheduleTemplate";

    private final ScheduleTemplateRepository scheduleTemplateRepository;

    public ScheduleTemplateResource(ScheduleTemplateRepository scheduleTemplateRepository) {
        this.scheduleTemplateRepository = scheduleTemplateRepository;
    }

    /**
     * POST  /schedule-templates : Create a new scheduleTemplate.
     *
     * @param scheduleTemplate the scheduleTemplate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scheduleTemplate, or with status 400 (Bad Request) if the scheduleTemplate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/schedule-templates")
    public ResponseEntity<ScheduleTemplate> createScheduleTemplate(@Valid @RequestBody ScheduleTemplate scheduleTemplate) throws URISyntaxException {
        log.debug("REST request to save ScheduleTemplate : {}", scheduleTemplate);
        if (scheduleTemplate.getId() != null) {
            throw new BadRequestAlertException("A new scheduleTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ScheduleTemplate result = scheduleTemplateRepository.save(scheduleTemplate);
        return ResponseEntity.created(new URI("/api/schedule-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /schedule-templates : Updates an existing scheduleTemplate.
     *
     * @param scheduleTemplate the scheduleTemplate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scheduleTemplate,
     * or with status 400 (Bad Request) if the scheduleTemplate is not valid,
     * or with status 500 (Internal Server Error) if the scheduleTemplate couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/schedule-templates")
    public ResponseEntity<ScheduleTemplate> updateScheduleTemplate(@Valid @RequestBody ScheduleTemplate scheduleTemplate) throws URISyntaxException {
        log.debug("REST request to update ScheduleTemplate : {}", scheduleTemplate);
        if (scheduleTemplate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ScheduleTemplate result = scheduleTemplateRepository.save(scheduleTemplate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scheduleTemplate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /schedule-templates : get all the scheduleTemplates.
     *
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of scheduleTemplates in body
     */
    @GetMapping("/schedule-templates")
    public ResponseEntity<List<ScheduleTemplate>> getAllScheduleTemplates(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of ScheduleTemplates");
        Page<ScheduleTemplate> page;
        if (eagerload) {
            page = scheduleTemplateRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = scheduleTemplateRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/api/schedule-templates?eagerload=%b", eagerload));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /all-schedule-templates : get all the schedule-templates.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of schedule-templates in body
     */
    @GetMapping("/all-schedule-templates")
    public ResponseEntity<List<ScheduleTemplate>> getAllBays() {
        log.debug("REST request to get a list of ScheduleTemplates");
        List<ScheduleTemplate> list = scheduleTemplateRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    /**
     * GET  /schedule-templates/:id : get the "id" scheduleTemplate.
     *
     * @param id the id of the scheduleTemplate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scheduleTemplate, or with status 404 (Not Found)
     */
    @GetMapping("/schedule-templates/{id}")
    public ResponseEntity<ScheduleTemplate> getScheduleTemplate(@PathVariable Long id) {
        log.debug("REST request to get ScheduleTemplate : {}", id);
        Optional<ScheduleTemplate> scheduleTemplate = scheduleTemplateRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(scheduleTemplate);
    }

    /**
     * DELETE  /schedule-templates/:id : delete the "id" scheduleTemplate.
     *
     * @param id the id of the scheduleTemplate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/schedule-templates/{id}")
    public ResponseEntity<Void> deleteScheduleTemplate(@PathVariable Long id) {
        log.debug("REST request to delete ScheduleTemplate : {}", id);
        scheduleTemplateRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
