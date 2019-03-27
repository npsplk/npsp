package lk.npsp.service;

import lk.npsp.task.ScheduleInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.TimeZone;

@Component
public class DateTimeCombiner {

    private final Logger log = LoggerFactory.getLogger(ScheduleInstantiator.class);

    public Instant combineDateAndTime(LocalDate date, Instant time) {

        ZoneId systemZone = ZoneId.systemDefault();
        Date dateFromLocalDate = Date.from(date.atStartOfDay(TimeZone.getTimeZone("Asia/Colombo").toZoneId()).toInstant());
        Date timeFromInstant = Date.from(time);
        String startingDate = new SimpleDateFormat("yyyy-MM-dd").format(dateFromLocalDate);
        String startingTime = new SimpleDateFormat("HH:mm:ss").format(timeFromInstant);
        LocalDate datePart = LocalDate.parse(startingDate);
        LocalTime timePart = LocalTime.parse(startingTime);
        LocalDateTime startingDateTime = LocalDateTime.of(datePart, timePart);
        ZoneOffset currentOffsetForLocalZone = systemZone.getRules().getOffset(dateFromLocalDate.toInstant());
        return startingDateTime.toInstant(currentOffsetForLocalZone);
    }
}
