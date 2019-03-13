package lk.npsp.service;

import lk.npsp.domain.ScheduleInstance;
import lk.npsp.domain.ScheduleTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ScheduleInstanceManager {

    public ScheduleInstance createFromTemplate(ScheduleInstance scheduleInstance,
                                               ScheduleTemplate scheduleTemplate) {
        Instant startTime = scheduleTemplate.getStartTime();

        scheduleInstance.setScheduledTime(startTime);
        scheduleInstance.setActualDepartureTime(startTime);
        scheduleInstance.setActualScheduledTime(startTime);
        scheduleInstance.setDriver(scheduleTemplate.getDriver());
        scheduleInstance.setBay(scheduleTemplate.getBay());
        scheduleInstance.setRoute(scheduleTemplate.getRoute());
        scheduleInstance.setVehicle(scheduleTemplate.getVehicle());
        scheduleInstance.setScheduleTemplate(scheduleTemplate);

        return scheduleInstance;
    }
}
