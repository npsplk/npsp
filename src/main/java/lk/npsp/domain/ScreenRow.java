package lk.npsp.domain;

import lk.npsp.domain.enumeration.ScheduleState;
import lk.npsp.domain.enumeration.ScreenLanguage;
import lk.npsp.service.SimpleTranslator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ScreenRow{
    private String time;
    private List<String> destination;
    private String route;
    private String remarks;
    private List<String> status;

    public ScreenRow(ScheduleInstance scheduleInstance, SimpleTranslator simpleTranslator){
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        this.time=dateFormat.format(Date.from(scheduleInstance.getActualScheduledTime()));

        Location destination=scheduleInstance.getScheduleTemplate().getRoute()
            .getRouteLocations().last().getLocation();
        this.destination = new ArrayList<>(Arrays.asList(
            destination.getLocationName(),
            destination.getLocationNameSinhala(),
            destination.getLocationNameTamil()
        ));
        this.route=scheduleInstance.getScheduleTemplate().getRoute().getRouteName();

        String scheduleStatus=scheduleInstance.getScheduleState().toString();
        this.status = new ArrayList<>(Arrays.asList(
            scheduleStatus,
            simpleTranslator.translate(scheduleStatus, ScreenLanguage.SINHALA),
            simpleTranslator.translate(scheduleStatus, ScreenLanguage.TAMIL)
        ));
        this.remarks="";
    }

    public String getTime(){return this.time;}
    public List<String> getDestination(){return this.destination;}
    public String getRoute(){return this.route;}
    public List<String> getStatus(){return this.status;}
    public String getRemarks(){return this.remarks;}
}
