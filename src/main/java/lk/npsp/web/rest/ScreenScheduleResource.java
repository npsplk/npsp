package lk.npsp.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import lk.npsp.domain.ScheduleInstance;
import lk.npsp.repository.ScheduleInstanceRepository;
import lk.npsp.web.rest.errors.BadRequestAlertException;
import lk.npsp.web.rest.util.HeaderUtil;
import lk.npsp.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ScheduleInstance.
 */
@RestController
@RequestMapping("/api")
public class ScreenScheduleResource {

    private final Logger log = LoggerFactory.getLogger(ScreenScheduleResource.class);

    private static final String ENTITY_NAME = "scheduleInstance";

    private final ScheduleInstanceRepository scheduleInstanceRepository;

    public ScreenScheduleResource(ScheduleInstanceRepository scheduleInstanceRepository) {
        this.scheduleInstanceRepository = scheduleInstanceRepository;
    }

    /**
     * GET  /screen-schedule : get all the scheduleInstances.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of scheduleInstances in body
     */
    @GetMapping("/screen/schedule-instances")
    public ResponseEntity<List<ScheduleInstance>> getSchedulesForScreen() {
        log.debug("REST request to get a Schedule for Screen");
        List<ScheduleInstance> list = scheduleInstanceRepository.findAll();
        return ResponseEntity.ok().body(list);
    }
}
