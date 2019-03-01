package lk.npsp.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScreenResponse {
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
