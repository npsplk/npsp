package lk.npsp.domain;

import lk.npsp.domain.enumeration.ScheduleState;
import lk.npsp.domain.enumeration.ScreenLanguage;
import lk.npsp.service.SimpleTranslator;

import java.text.SimpleDateFormat;
import java.util.*;

public class ScreenRow {
    private String time;
    private List<String> bay;
    private List<String> destination;
    private String route;
    private String remarks;
    private List<String> status;
    private String transportType;

    public ScreenRow(ScheduleInstance scheduleInstance, SimpleTranslator simpleTranslator) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Colombo"));
        this.time = dateFormat.format(Date.from(scheduleInstance.getActualScheduledTime()));

        Location destination = scheduleInstance.getScheduleTemplate().getRoute()
            .getRouteLocations().last().getLocation();
        String bayName = scheduleInstance.getBay().getBayName().replace("Bay ", "");
        this.bay = new ArrayList<>(Arrays.asList(
            bayName,
            simpleTranslator.translate(bayName, ScreenLanguage.SINHALA),
            simpleTranslator.translate(bayName, ScreenLanguage.TAMIL)
        ));
        this.destination = new ArrayList<>(Arrays.asList(
            destination.getLocationName(),
            destination.getLocationNameSinhala(),
            destination.getLocationNameTamil()
        ));
        this.route = scheduleInstance.getScheduleTemplate().getRoute().getRouteNumber();

        String scheduleStatus = scheduleInstance.getScheduleState().toString();
        this.status = new ArrayList<>(Arrays.asList(
            scheduleStatus,
            simpleTranslator.translate(scheduleStatus, ScreenLanguage.SINHALA),
            simpleTranslator.translate(scheduleStatus, ScreenLanguage.TAMIL)
        ));
        this.remarks = "";

        this.transportType=scheduleInstance.getVehicle().getTransportType().getMetaCode();
    }

    public String getTime() {
        return this.time;
    }

    public String getTransportType() {
        return this.transportType;
    }

    public List<String> getBay() {
        return this.bay;
    }

    public List<String> getDestination() {
        return this.destination;
    }

    public String getRoute() {
        return this.route;
    }

    public List<String> getStatus() {
        return this.status;
    }

    public String getRemarks() {
        return this.remarks;
    }
}
