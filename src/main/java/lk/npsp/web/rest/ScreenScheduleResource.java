package lk.npsp.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import lk.npsp.domain.ScheduleInstance;
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

class ScreenResponse{

    private static final int MAX_ROW_LIMIT = 5;

    private String currentDate;
    private String screenTitle;
    private List<ScreenRow> screenRows=new ArrayList<>();

    public ScreenResponse(List<ScheduleInstance> scheduleInstanceList){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
        Date today= new Date();

        this.currentDate=dateFormat.format(today);
        this.screenTitle="Bay 01 - Departures";

        int list_limit = scheduleInstanceList.size()<MAX_ROW_LIMIT ? scheduleInstanceList.size():MAX_ROW_LIMIT;

        scheduleInstanceList.sort(new SortByScheduleTime());

        for (int i=0; i<list_limit;i++){
            ScheduleInstance scheduleInstance=scheduleInstanceList.get(i);
            ScreenRow screenRow= new ScreenRow(scheduleInstance);

            this.screenRows.add(screenRow);
        }

    }

    public String getCurrentDate(){return this.currentDate;}
    public String getScreenTitle(){return this.screenTitle;}
    public List<ScreenRow> getScreenRows(){return this.screenRows;}
}

class SortByScheduleTime implements Comparator<ScheduleInstance>
{
    public int compare(ScheduleInstance a, ScheduleInstance b)
    {
        return a.getActualScheduledTime().compareTo(b.getActualScheduledTime());
    }
}

class ScreenRow{
    private String time;
    private String destination;
    private String route;
    private String remarks;
    private ScheduleState status;

    public ScreenRow(ScheduleInstance scheduleInstance){
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");

        this.time=dateFormat.format(Date.from(scheduleInstance.getActualScheduledTime()));
        this.destination=scheduleInstance.getScheduleTemplate().getRoute().getRouteName();
        this.route=scheduleInstance.getScheduleTemplate().getRoute().getRouteName();
        this.status=ScheduleState.PENDING;
        this.remarks="";
    }

    public String getTime(){return this.time;}
    public String getDestination(){return this.destination;}
    public String getRoute(){return this.route;}
    public String getStatus(){return this.status.toString();}
    public String getRemarks(){return this.remarks;}
}

enum ScheduleState{
    ARRIVED,PENDING,DELAYED,CANCELLED
}
