package lk.npsp.domain;

import lk.npsp.domain.enumeration.ScheduleState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ScreenRow{
    private String time;
    private String destination;
    private String route;
    private String remarks;
    private List<String> status;

    public ScreenRow(ScheduleInstance scheduleInstance){
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");

        this.time=dateFormat.format(Date.from(scheduleInstance.getActualScheduledTime()));
        this.destination=scheduleInstance.getScheduleTemplate().getRoute().getRouteName();
        this.route=scheduleInstance.getScheduleTemplate().getRoute().getRouteName();
        this.status = new ArrayList<>(Arrays.asList(
            scheduleInstance.getScheduleState().toString(),
            "පර්යන්ත 01 - පිටත්වීම්",
            "டெர்மினல்கள் 01 - புறப்பாடு"
        ));
        this.remarks="";
    }

    public String getTime(){return this.time;}
    public String getDestination(){return this.destination;}
    public String getRoute(){return this.route;}
    public List<String> getStatus(){return this.status;}
    public String getRemarks(){return this.remarks;}
}
