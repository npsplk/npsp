package lk.npsp.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import lk.npsp.domain.ScheduleInstance;
import lk.npsp.domain.ScreenResponse;
import lk.npsp.repository.ScreenScheduleRepository;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * REST controller for managing ScheduleInstance.
 */
@RestController
@RequestMapping("/api/screen")
public class ScreenScheduleResource {

    private final Logger log = LoggerFactory.getLogger(ScreenScheduleResource.class);

    private static final String ENTITY_NAME = "scheduleInstance";

    private final ScreenScheduleRepository screenScheduleRepository;

    public ScreenScheduleResource(ScreenScheduleRepository screenScheduleRepository) {
        this.screenScheduleRepository = screenScheduleRepository;
    }

    /**
     * GET  /schedule : get all the schedules for screen.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of scheduleInstances in body
     */
    @GetMapping("/schedule")
    public ResponseEntity<ScreenResponse> getSchedulesForScreen() {
        log.debug("REST request to get a Schedule for Screen");
        List<ScheduleInstance> list = screenScheduleRepository.findScheduleInstancesByScreen();

        ScreenResponse screenResponse= new ScreenResponse(list);

        return ResponseEntity.ok().body(screenResponse);
    }
}
