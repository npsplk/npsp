package lk.npsp.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import javassist.NotFoundException;
import lk.npsp.domain.Bay;
import lk.npsp.domain.ScheduleInstance;
import lk.npsp.domain.ScreenResponse;
import lk.npsp.repository.BayRepository;
import lk.npsp.repository.ScreenScheduleRepository;
import lk.npsp.service.ResourceLocator;
import lk.npsp.service.SimpleTranslator;
import lk.npsp.web.rest.errors.BadRequestAlertException;
import lk.npsp.web.rest.util.HeaderUtil;
import lk.npsp.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * REST controller for managing ScheduleInstance.
 */
@RestController
@RequestMapping("/api/screen")
public class ScreenScheduleResource {

    private final Logger log = LoggerFactory.getLogger(ScreenScheduleResource.class);

    private static final Long SCHEDULE_DELAY_PADDING_IN_SECONDS = Integer.toUnsignedLong(1800);

    private final ScreenScheduleRepository screenScheduleRepository;
    private final BayRepository bayRepository;

    private final SimpleTranslator simpleTranslator;
    private final ResourceLocator resourceLocator;

    public ScreenScheduleResource(ScreenScheduleRepository screenScheduleRepository, BayRepository bayRepository,
                                  SimpleTranslator simpleTranslator, ResourceLocator resourceLocator) {
        this.screenScheduleRepository = screenScheduleRepository;
        this.bayRepository = bayRepository;
        this.simpleTranslator= simpleTranslator;
        this.resourceLocator = resourceLocator;
    }

    /**
     * GET  /schedule : get all the schedules for screen.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of scheduleInstances in body
     * @throws IOException if the tableheaders configuration is not loaded properly
     */
    @CrossOrigin
    @GetMapping("/schedule")
    public ResponseEntity<ScreenResponse> getSchedulesForScreen(HttpServletRequest request) throws IOException {
        log.debug("REST request to get a Schedule for Screen");
        String ipAddress = request.getRemoteAddr();

        Optional<Bay> bayOptional= bayRepository.findOneBayByIP(ipAddress);

        //TODO: uncomment for production
//        if(!bayOptional.isPresent()){
//            throw new BadRequestAlertException("IP address was not recognized", ENTITY_NAME, "bay ip not found "
//                + ipAddress);
//        }

        Long bayId= bayOptional.map(Bay::getId).orElse(Integer.toUnsignedLong(1));
        String bayName= bayOptional.map(Bay::getBayName).orElse("Bay 01");

        Instant now= Instant.now(); //get schedules after current time
        List<ScheduleInstance> list = screenScheduleRepository.findScheduleInstancesByScreen
            (bayId,now.minusSeconds(SCHEDULE_DELAY_PADDING_IN_SECONDS));
        ScreenResponse screenResponse = new ScreenResponse(list, bayName, simpleTranslator, resourceLocator);

        return ResponseEntity.ok().body(screenResponse);
    }

    /**
     * GET  /schedule-summary : get all the schedules for summary screen.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of scheduleInstances in body
     * @throws IOException if the tableheaders configuration is not loaded properly
     */
    @CrossOrigin
    @GetMapping("/schedule-summary")
    public ResponseEntity<ScreenResponse> getSchedulesForSummary(HttpServletRequest request) throws IOException {
        log.debug("REST request to get a Schedule for Summary");

        Instant now= Instant.now(); //get schedules after current time
        List<ScheduleInstance> list = screenScheduleRepository.findScheduleInstancesByDay
            (now.minusSeconds(SCHEDULE_DELAY_PADDING_IN_SECONDS));
        ScreenResponse screenResponse = new ScreenResponse(list, "", simpleTranslator, resourceLocator);

        return ResponseEntity.ok().body(screenResponse);
    }
}
