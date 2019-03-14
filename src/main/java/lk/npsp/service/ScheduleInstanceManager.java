package lk.npsp.service;

import lk.npsp.domain.ScheduleInstance;
import lk.npsp.domain.ScheduleTemplate;
import org.springframework.stereotype.Service;
import java.time.*;

@Service
public class ScheduleInstanceManager {

    public ScheduleInstance createFromTemplate(ScheduleInstance scheduleInstance,
                                               ScheduleTemplate scheduleTemplate) {

        Instant startingInstant = new DateTimeCombiner().combineDateAndTime
            (scheduleInstance.getDate(), scheduleTemplate.getStartTime());


        scheduleInstance.setScheduledTime(startingInstant);
        scheduleInstance.setActualDepartureTime(startingInstant);
        scheduleInstance.setActualScheduledTime(startingInstant);
        scheduleInstance.setDriver(scheduleTemplate.getDriver());
        scheduleInstance.setBay(scheduleTemplate.getBay());
        scheduleInstance.setRoute(scheduleTemplate.getRoute());
        scheduleInstance.setVehicle(scheduleTemplate.getVehicle());
        scheduleInstance.setScheduleTemplate(scheduleTemplate);

        return scheduleInstance;
    }
}
