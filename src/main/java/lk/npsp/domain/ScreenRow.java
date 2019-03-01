package lk.npsp.domain;

import lk.npsp.domain.enumeration.ScheduleState;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenRow{
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
